from django.http import HttpResponse, JsonResponse
from django.shortcuts import render

# Create your views here.
import requests
import json
from django.shortcuts import render, redirect
from django.contrib import messages
from django.views.decorators.csrf import csrf_exempt
import os

API_BASE_URL = "http://localhost:8080/user"


def signin(request):
    if request.method == 'POST':
        # Récupérer les données du formulaire
        email = request.POST.get('email')
        password = request.POST.get('password')

        # Construire la requête à l'API
        data = {
            'email': email,
            'password': password
        }
        response = requests.post(f"{API_BASE_URL}/auth/signin", data=data)

        if response.status_code == 200:
            # Stocker l'email dans un cookie valide pour 1 heure
            response = redirect('home')  # Redirige vers la page d'accueil
            response.set_cookie('email', email, max_age=3600, httponly=True)  # Cookie sécurisé et HTTPOnly
            messages.success(request, "Connexion réussie !")
            return response
        else:
            messages.error(request, response.json().get('message', 'Erreur lors de la connexion'))

    return render(request, 'accounts/signin.html')

def home(request):
    # Ajoutez ici des données dynamiques si nécessaire
    return render(request, 'accounts/home.html')

def signup(request):
    if request.method == 'POST':
        # Récupérer les données du formulaire
        email = request.POST.get('email')
        password = request.POST.get('password')

        # Construire la requête à l'API
        data = {
            'email': email,
            'password': password,
        }
        response = requests.post(f"{API_BASE_URL}/auth/signup", data=data)

        if response.status_code == 200:
            messages.success(request, "Inscription réussie ! Connectez-vous maintenant.")
            return redirect('signin')
        else:
            messages.error(request, response.json().get('message', 'Erreur lors de l’inscription'))

    return render(request, 'accounts/signup.html')


def create_folder(request):
    """Crée un dossier via l'API."""
    if request.method == 'POST':
        # Récupération des données multipart/form-data
        idUser = request.POST.get('idUser')
        path = request.POST.get('path')
        folderName = request.POST.get('folderName')

        data = {'idUser': idUser, 'path': path, 'folderName': folderName}

        try:
            # Appel à l'API externe
            response = requests.post(f"{API_BASE_URL}/func/createFolder", data=data)
            if response.status_code == 200:
                # Redirige vers la page d'accueil après succès
                return JsonResponse({"message": "Dossier créé avec succès !", "redirect": True}, status=200)
            else:
                return JsonResponse(response.json(), status=response.status_code)
        except requests.RequestException as e:
            return JsonResponse({"error": f"Erreur réseau : {str(e)}"}, status=500)
    return render(request, 'accounts/create_folder.html')


def deposit_file(request):
    """Dépose un fichier via l'API"""
    if request.method == 'POST':
        # Vérifier si un fichier est bien présent dans le formulaire
        if 'file' not in request.FILES:
            messages.error(request, "Aucun fichier sélectionné.")
            return redirect('deposit_file')

        email = request.POST.get('email')  # Récupérer l'email
        path = request.POST.get('path')  # Récupérer le chemin
        name = request.POST.get('name')  # Récupérer le nom du fichier
        uploaded_file = request.FILES['file']  # Récupérer le fichier téléchargé

        # Créer un dictionnaire pour les données à envoyer à l'API
        data = {
            'idUser': email,
            'path': path,
            'fileName': name
        }

        # Créer un fichier form-data avec le fichier et les autres informations
        files = {
            'file': (name, uploaded_file, uploaded_file.content_type)
        }

        try:
            # Envoi de la requête POST à l'API pour déposer le fichier
            response = requests.post(f"{API_BASE_URL}/func/deposit", data=data, files=files)

            # Vérifier si la réponse est correcte
            if response.status_code == 200:
                data = response.json()  # Parse la réponse JSON
                if data.get("message"):
                    messages.success(request, f"Fichier déposé avec succès : {data['message']}")
                    return redirect('home')
                else:
                    messages.error(request, "Erreur dans la réponse de l'API.")
            else:
                messages.error(request, f"Erreur API : {response.status_code} - {response.text}")
        except requests.RequestException as e:
            messages.error(request, f"Erreur lors de l'envoi du fichier : {e}")

    return render(request, 'accounts/deposit_file.html')

def download_file(request):
    """Télécharge un fichier via l'API."""
    if request.method == 'GET':
        # Récupère les paramètres envoyés dans l'URL
        email = request.GET.get('idUser')
        path = request.GET.get('path')
        name = request.GET.get('fileName')

        data = {'email': email, 'path': path, 'name': name}

        try:
            # Utilise GET pour appeler l'API avec les paramètres dans l'URL
            response = requests.get(f"{API_BASE_URL}/func/download", params=data, stream=True)

            if response.status_code == 200:
                # Renvoie le fichier téléchargé à l'utilisateur
                content_type = response.headers.get('Content-Type', 'application/octet-stream')
                content_disposition = response.headers.get('Content-Disposition', f'attachment; filename="{name}"')
                return HttpResponse(response.content, content_type=content_type, headers={
                    'Content-Disposition': content_disposition
                })
            else:
                # Si une erreur survient dans la requête API, affiche un message
                messages.error(request, "Échec du téléchargement du fichier.")
                return render(request, 'accounts/download_file.html')
        except requests.RequestException as e:
            # Si une erreur réseau survient
            messages.error(request, f"Erreur réseau : {e}")
            return render(request, 'accounts/download_file.html')

    return render(request, 'accounts/download_file.html')


def delete_file(request):
    """Supprime un fichier via l'API."""
    if request.method == 'POST':
        email = request.POST.get('email')
        path = request.POST.get('path')
        fileName = request.POST.get('fileName')

        data = {'idUser': email, 'path': path, 'fileName': fileName}

        try:
            response = requests.post(f"{API_BASE_URL}/func/deleteFile", data=data)
            if response.status_code == 200:
                messages.success(request, "Fichier supprimé avec succès !")
                return redirect('home')
            else:
                messages.error(request, "Erreur lors de la suppression du fichier.")
                return render(request, 'accounts/delete_file.html')
        except requests.RequestException as e:
            messages.error(request, f"Erreur réseau : {e}")
            return render(request, 'accounts/delete_file.html')

    return render(request, 'accounts/delete_file.html')


def delete_folder(request):
    """Supprime un dossier via l'API."""
    if request.method == 'POST':
        email = request.POST.get('email')
        path = request.POST.get('path')
        folder_name = request.POST.get('folder_name')

        data = {'idUser': email, 'path': path, 'folderName': folder_name}

        try:
            response = requests.post(f"{API_BASE_URL}/func/deleteFolder", data=data)
            if response.status_code == 200:
                messages.success(request, "Dossier supprimé avec succès !")
                return redirect('home')
            else:
                messages.error(request, "Erreur lors de la suppression du dossier.")
                return render(request, 'accounts/delete_folder.html')
        except requests.RequestException as e:
            messages.error(request, f"Erreur réseau : {e}")
            return render(request, 'accounts/delete_folder.html')

    return render(request, 'accounts/delete_folder.html')

'''
def home(request):
    api_url = "http://localhost:8080/user/func/getHome"

    # Récupère l'email depuis les cookies
    user_email = request.COOKIES.get('email')
    print(f"Email récupéré dans les cookies : {user_email}")

    if not user_email:
        messages.error(request, "Vous devez vous connecter pour accéder à cette page.")
        return redirect('signin')  # Redirige vers la page de connexion si pas d'email

    headers = {"Content-Type": "multipart/form-data"}
    data = {"email": user_email}

    try:
        response = requests.post(api_url, data=data, headers=headers)
        response.raise_for_status()
        file_tree_string = response.json().get("data")  # Ajustez selon la réponse API
        file_tree = json.loads(file_tree_string)
    except (requests.exceptions.RequestException, json.JSONDecodeError) as e:
        print(f"Erreur : {e}")
        messages.error(request, "Impossible de récupérer l'arborescence.")
        file_tree = {}

    return render(request, 'home.html', {'file_tree': file_tree})
'''
