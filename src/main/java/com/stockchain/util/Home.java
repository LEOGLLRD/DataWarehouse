package com.stockchain.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class Home {

    String idUser;
    Folder folder;

    public Home(String idUser) {
        this.idUser = idUser;
        this.folder = new Folder("home");
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public static Home jsonToHome(String json) {

        System.out.println("base json : " + json);
        //First getting the root folder
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        String idU = (jsonObject.get("idUser").getAsString());
        Home home = new Home(idU);
        String rootString = String.valueOf(jsonObject.get("folder"));
        home.setFolder(jsonToFolder(rootString));
        return home;
    }

    public static Folder jsonToFolder(String json) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(json).getAsJsonObject();
        Folder folder = new Folder(jsonObject.get("name").getAsString());
        //Going through the contains array
        for (JsonElement contained : jsonObject.get("contains").getAsJsonArray()) {
            JsonObject containedObject = contained.getAsJsonObject();
            //Checking if there are some folder in the current folder
            //by checking if ther is a "name" attribute
            if (containedObject.has("name")) {
                folder.addFolder(jsonToFolder(String.valueOf(containedObject)));
            } else if (containedObject.has("filename")) {
                folder.addFile(new File(containedObject.get("filename").getAsString(), containedObject.get("id").getAsInt()));
            }
        }

        return folder;
    }

}