package com.stockchain.util;


import java.util.ArrayList;
import java.util.List;

public class Folder implements Source {

    //     root/fol1/fol2/file

    String name;
    List<Source> contains;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Source> getContains() {
        return contains;
    }

    public Folder() {
    }

    public Folder(String name) {
        this.name = name;
        this.contains = new ArrayList<Source>();
    }

    public boolean addFile(File file) {
        //First we need to check if a file with the same name already exists
        for (Source s : contains) {
            if (s instanceof File) {
                File f = (File) s;
                if (f.getFilename().equals(file.getFilename())) {
                    System.out.println(file.getFilename() + " already exists");
                    return false;
                }
                continue;

            } else continue;
        }
        this.contains.add(file);
        return true;
    }


    public boolean removeFile(String filename) {
        for (Source s : contains) {
            if (s instanceof File) {
                File f = (File) s;
                if (f.getFilename().equals(filename)) {
                    System.out.println(filename + " removed");
                    this.contains.remove(f);
                    return true;
                }
            } else continue;
        }
        System.out.println(filename + " not found");
        return false;
    }

    //Add a file at the given path
    //The path given is supposed to be already in /home
    //So no need to provide the "/home"
    //
    //Example :
    //      To add the file "file1" at "/home/folder1"
    //      Path must be : "folder1"

    //If no path given, the file is added in the current folder
    public boolean addFileAt(File file, String path) {

        if (path == null || path.isEmpty()) {
            return false;
        }
        String[] folders = path.split("/");
        if (folders.length == 1) {
            this.addFile(file);
            return true;
        }
        //Setting the first folder to go through which is this at beginning
        Folder currentFolder = this;
        List<Source> list = currentFolder.getContains();
        //Going through all the path
        System.out.println("length : " + folders.length);

        for (int i = 0; i < folders.length; i++) {
            System.out.println("i : " + i);

            //Setting a variable to know if we found the folder at each iteration
            boolean found = false;
            String folder = folders[i];
            System.out.println("Folder " + folder);
            System.out.println(folder);
            for (Source s : list) {
                if (s instanceof Folder) {
                    //Checking if the s folder is the one want to go through after
                    if (((Folder) s).getName().equals(folder)) {

                        currentFolder = (Folder) s;
                        list = currentFolder.getContains();
                        found = true;
                        break;
                    }
                } else continue;
            }

            //Checking that we found the folder, if not we return false
            if (!found) {
                return false;
            }

            //Checking if we are at the end of the path
            if (i == folders.length - 1) {
                System.out.println("Finished");
                currentFolder.addFile(file);
                return true;
            }
        }
        return false;

    }

    //Remove a file at the given path
    //The path given is supposed to be already in /home
    //So no need to provide the "/home"
    //
    //Example :
    //      To remove the file "file1" at "/home/folder1"
    //      Path must be : "folder1"
    public boolean removeFileAt(String name, String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        String[] folders = path.split("/");
        if (folders.length == 1) {
            this.removeFile(name);
            return true;
        }

        //Setting the first folder to go through which is this at beginning
        Folder currentFolder = this;
        List<Source> list = currentFolder.getContains();

        //Going through all the path
        for (int i = 0; i < folders.length; i++) {
            //Setting a variable to know if we found the folder at each iteration
            boolean found = false;
            String folder = folders[i];
            System.out.println(folder);
            for (Source s : list) {
                if (s instanceof Folder) {
                    //Checking if the s folder is the one want to go through after
                    if (((Folder) s).getName().equals(folder)) {

                        currentFolder = (Folder) s;
                        list = currentFolder.getContains();
                        found = true;
                        break;
                    }
                } else continue;
            }

            //Checking that we found the folder, if not we return false
            if (!found) {
                return false;
            }

            //Checking if we are at the end of the path
            if (i == folders.length - 1) {
                System.out.println("Finished");
                currentFolder.removeFile(name);
                return true;
            }
        }
        return false;

    }

    public File getFile(String name) {
        for (Source s : contains) {
            if (s instanceof File) {
                File f = (File) s;
                if (f.getFilename().equals(name)) {
                    return f;
                }
            }
        }
        return null;
    }

    //Return a file at the given path
    //The path given is supposed to be already in /home
    //So no need to provide the "/home"
    //
    //Example :
    //      To get the file "file1" at "/home/folder1"
    //      Path must be : "folder1"
    public File getFileAt(String name, String path) {
        String[] folders = path.split("/");

        if (path == null || path.isEmpty()) {
            return null;
        }

        if (folders.length == 1) {
            return this.getFile(name);
        }
        Folder currentFolder = this;
        List<Source> list = currentFolder.getContains();
        for (int i = 0; i < folders.length; i++) {
            boolean found = false;
            String folder = folders[i];
            System.out.println(folder);
            for (Source s : list) {
                if (s instanceof Folder) {
                    if (((Folder) s).getName().equals(folder)) {
                        currentFolder = (Folder) s;
                        list = currentFolder.getContains();
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return null;
            }
            if (i == folders.length - 1) {
                System.out.println("Finished");
                currentFolder.getFile(name);
            }
        }
        return null;
    }

    public boolean addFolder(Folder folder) {
        //First we need to check if a file with the same name already exists
        for (Source s : contains) {
            if (s instanceof Folder) {
                Folder f = (Folder) s;
                if (f.getName().equals(folder.getName())) {
                    System.out.println(folder.getName() + " already exists");
                    return false;
                }
                continue;

            } else continue;
        }
        this.contains.add(folder);
        return true;
    }

    public boolean removeFolder(String folder) {
        for (Source s : contains) {
            if (s instanceof Folder) {
                Folder f = (Folder) s;
                if (f.getName().equals(folder)) {
                    System.out.println(folder + " removed");
                    this.contains.remove(f);
                    return true;
                }
            } else continue;
        }
        System.out.println(folder + " not found");
        return false;
    }

    public Folder getFolder(String name) {
        for (Source s : contains) {
            if (s instanceof Folder) {
                Folder f = (Folder) s;
                if (f.getName().equals(name)) {
                    return f;
                }
            }
        }
        return null;
    }

    public Folder getFolderAt(String name, String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        String[] folders = path.split("/");
        if (folders.length == 1) {
            return this.getFolder(name);
        }
        Folder currentFolder = this;
        List<Source> list = currentFolder.getContains();
        for (int i = 0; i < folders.length; i++) {
            boolean found = false;
            String folder = folders[i];
            System.out.println(folder);
            for (Source s : list) {
                if (s instanceof Folder) {
                    if (((Folder) s).getName().equals(folder)) {
                        currentFolder = (Folder) s;
                        list = currentFolder.getContains();
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return null;
            }
            if (i == folders.length - 1) {
                System.out.println("Finished");
                return currentFolder;
            }
        }
        return null;
    }

    public boolean removeFolderAt(String name, String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        String[] folders = path.split("/");
        if (folders.length == 1) {
            this.removeFolder(name);
            return true;
        }
        Folder currentFolder = this;
        List<Source> list = currentFolder.getContains();
        for (int i = 0; i < folders.length; i++) {
            boolean found = false;
            String folder = folders[i];
            System.out.println(folder);
            for (Source s : list) {
                if (s instanceof Folder) {
                    if (((Folder) s).getName().equals(folder)) {
                        currentFolder = (Folder) s;
                        list = currentFolder.getContains();
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return false;
            }
            if (i == folders.length - 1) {
                currentFolder.removeFolder(name);
                return true;
            }
        }
        return false;
    }

    public boolean addFolderAt(Folder f, String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        String[] folders = path.split("/");
        if (folders.length == 1) {
            this.addFolder(f);
            return true;
        }
        Folder currentFolder = this;
        List<Source> list = currentFolder.getContains();
        for (int i = 0; i < folders.length; i++) {
            boolean found = false;
            String folder = folders[i];
            System.out.println(folder);
            for (Source s : list) {
                if (s instanceof Folder) {
                    if (((Folder) s).getName().equals(folder)) {
                        currentFolder = (Folder) s;
                        list = currentFolder.getContains();
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                return false;
            }
            if (i == folders.length - 1) {
                currentFolder.addFolder(f);
                return true;
            }
        }
        return false;
    }


    public String toString() {
        return "";
    }

}
