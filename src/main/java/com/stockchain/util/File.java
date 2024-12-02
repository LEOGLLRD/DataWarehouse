package com.stockchain.util;


import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Setter
@Getter
public class File implements Source {
    String filename;
    String id;
    long size;

    public File() {
    }

    public File(String filename, String id) {
        this.filename = filename;
        this.id = id;
    }

    public File(String filename, String id, long size) {
        this.filename = filename;
        this.id = id;
        this.size = size;
    }

}