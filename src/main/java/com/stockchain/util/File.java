package com.stockchain.util;


public class File implements Source {
    String filename;
    String id;

    public File() {
    }

    public File(String filename, String id) {
        this.filename = filename;
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}