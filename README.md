# Required before launching the application

## MongoDB

On localhost:27017 <br>
With Database name = datawarehouse <br>
You can change the port or the database name via the application.properties in "src/main/resources" <br>
2 collections are required :
<ol>
<li>home</li>
<li>user</li>
</ol>

## Django

Run manage.py in Django/Front folder

# How to use each method

## localhost:8080/user/auth/signup

Used to register yourself. It creates you an account, and a home.

Required
<ol>
<li>email</li>
<li>password</li>
</ol>

## localhost:8080/user/auth/signin

Not useful there because no token system. <br>
Required
<ol>
<li>email</li>
<li>password</li>
</ol>

## localhost:8080/user/func/deposit

Required
<ol>
<li>name : name of the file</li>
<li>idUser : email of the user (used as id in mongoDB)</li>
<li>file : the file selected via postman or the web site</li>
<li>path : path where you want to store the file</li>
for the path, if you want to store in your home, just set "home", if you want to go further, you don't need to precise the "home".
For example if you want to deposit at the path "home/folder", just precise "folder".
</ol>

## localhost:8080/user/func/createFolder

Required
<ol>
<li>idUser : email of the user (used as id in mongoDB)</li>
<li>folderName : name of the folder you want to create</li>
<li>path : path where you want to create the folder</li>
for the path, if you want to store in your home, just set "home", if you want to go further, you don't need to precise the "home".
For example if you want to deposit at the path "home/folder", just precise "folder".
</ol>

## localhost:8080/user/func/deleteFolder

Required
<ol>
<li>idUser : email of the user (used as id in mongoDB)</li>
<li>folderName : name of the folder you want to delete</li>
<li>path : path where you want to deleter the folder</li>
for the path, if you want to store in your home, just set "home", if you want to go further, you don't need to precise the "home".
For example if you want to deposit at the path "home/folder", just precise "folder".
</ol>

## localhost:8080/user/func/deleteFile

Required
<ol>
<li>idUser : email of the user (used as id in mongoDB)</li>
<li>fileName : name of the file you want to delete</li>
<li>path : path where you want to delete the file</li>
for the path, if you want to store in your home, just set "home", if you want to go further, you don't need to precise the "home".
For example if you want to deposit at the path "home/folder", just precise "folder".
</ol>

## localhost:8080/user/func/download

Required
<ol>
<li>idUser : email of the user (used as id in mongoDB)</li>
<li>fileName : name of the file you want to download</li>
<li>path : path where you want to download the file</li>
for the path, if you want to store in your home, just set "home", if you want to go further, you don't need to precise the "home".
For example if you want to deposit at the path "home/folder", just precise "folder".
</ol>

## localhost:8080/user/func/getHome

Return the home tree of the user as string.

Required
<ol>
<li>idUser : email of the user (used as id in mongoDB)</li>
</ol>

## localhost:8080/user/stats/filesNb

Return the number of files stored for the user.

Required
<ol>
<li>idUser : email of the user (used as id in mongoDB)</li>
</ol>

## localhost:8080/user/stats/filesSize

Return the size in byte of the user's home.

Required
<ol>
<li>idUser : email of the user (used as id in mongoDB)</li>
</ol>

# About the front

The front application couldn't be finished because we got some problem with using Django framework, but all the endpoints are usable.
Most of the endpoints are usable by the front application.
For the ones that are not usable, you can still use them by Postman for example.

# Utils

You can find the PostMan collection in the root folder of the project as a Json file if you want to import it in Postman
and have all the requests.

You can find the "Cahier des charges" in the root folder of the project as a markdown file.
