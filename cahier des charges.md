# Idée principale du projet

Le but du projet est de réaliser un stockage en ligne basé sur MongoDB.
Pour se faire nous utilsons Java et nous nous basons sur Spring pour réaliser le Back-end. 
Le front-end lui est réalisé avec Python et Django.

# Pourquoi Spring

Spring est un framework Java permettant de facilement créer et gérer des API, et c'est un framework sur lequel nous avions déjà une certaine experience avant de commencer le projet.

Il permet de facilement gérer toute base de données, comme MongoDB par exemple.
De plus j'ai (Léo) pu observer qu'il était suffisamment robuste et fluide pour être utilisé pour des transactions bancaires durant mon stage, qui est un domaine dans lequel le nombre de transactions par minutes peut être très grand, ce qui nous rassure encore plus dans le fait qu'il puisse en théorie gérer des transactions de fichiers à grande échelle.

# Pourquoi MongoDB

MongoDb est une base de données document. Cette structure de base de données permet une grande flexibilité sur la structure de nos différentes collections.
C'est utile pour nous ici, car dans notre cas si l'on souhaite par exemple ajouter à notre système de stockage la possibilité de partager les fichiers entres utilisateurs, en donnant des accès par exemple. Il suffit d'ajouter un champ contenant les id des utilisateurs ayant accès au fichier en question.
C'est un exemple parmi tant d'autres, mais c'est la principale force de la base de données Document : La Flexibilité !

De plus via l'utilisation de MongoDB avec Spring, le framework nous met à disposition plusieurs librairies dont une qui s'appel GridFS qui permet de gérer l'upload et le téléchargement de fichiers sur MongoDB.
Cette librairie créé deux collections dans MongoDB : FsFiles et FsChunks, qui permettent ensemble de stocker des fichiers sous forme binaire découpées. Ce qui indirectement rend les fichiers dure à récupérer via une simple requête sur MongoDB. Permettant une certaine sécurité supplémentaire.
Dans ce projet, nous nous sommes passés de plusieurs sécurités afin de nous concentrer sur le plus important qui est la base de données.

# Cahier des charges 

| Objectif                                                                                 | Description                                                                                     | Priorité |
|:-----------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------|----------|
| Permettre l'enregistrement d'utilisateurs                                                | Enregistrer en base un utilisateur, et lui créer un home pour qu'il puisse déposer des fichiers | Haute    |
| Permettre le dépot de fichier à un chemin spécific                                       | Stocker un fichier à un endroit spécific dans l'arbre home de l'utilisateur                     | Haute    |
| Permettre le téléchargement d'un fichier déposé à un chemin spécific                     | Permettre de récuperer un fichier à un endroit spécific dans l'arbre home de l'utilisateur      | Haute    |
| Permettre la suppression d'un fichier déposé à un chemin spécific                        | Permettre de supprimer un fichier à un endroit spécific dans l'arbre home de l'utilisateur      | Haute    |
| Permettre la création d'un dossier à un chemin spécific                                  | Permettre de créer un dossier à un chemin spécifié par l'utilisateur dans son arbre home        | Haute    |
| Permettre la suppression d'un dossier à un chemin spécific                               | Permettre de supprimer un dossier à un chemin spécifié par l'utilisateur dans son arbre home    | Haute    |
| Permettre à l'utilisateur de voir son home complet                                       | Afficher à l'utilisateur son arborescence de fichier complète                                   | Haute    |
| Permettre à l'utilisateur de voir combien de fichier il a déposé                         | Afficher à l'utilisateur le nombre de fichier qu'il a déposé                                    | Moyenne  |
| Permettre à l'utilisateur de voir la taille de son arborescence de fichier               | Afficher à l'utilisateur la taille en octet de son arborescence de fichier                      | Moyenne  |
| Permettre à l'utilisateur d'accéder aux fonctions citées plus haut via une interface web | Afficher à l'utilisateur la taille en octet de son arborescence de fichier                      | Moyenne  |
