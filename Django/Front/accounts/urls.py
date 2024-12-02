from django.urls import path
from . import views

urlpatterns = [
    path('signup/', views.signup, name='signup'),
    path('signin/', views.signin, name='signin'),
    path('home/', views.home, name='home'),
    path('create_folder/', views.create_folder, name='create_folder'),
    path('deposit_file/', views.deposit_file, name='deposit_file'),
    path('download_file/', views.download_file, name='download_file'),
    path('delete_file/', views.delete_file, name='delete_file'),
    path('delete_folder/', views.delete_folder, name='delete_folder'),
]
