package com.stockchain.util;


public class File implements Source {
    String filename;
    int id;

    public File() {
    }

    public File(String filename, int id) {
        this.filename = filename;
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}