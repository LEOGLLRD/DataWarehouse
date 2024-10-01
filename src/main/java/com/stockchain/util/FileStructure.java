package com.stockchain.util;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
public class FileStructure implements Serializable {

    private static final long serialVersionUID = 12378467319848962L;
    byte[] array;
    String fileName;

    public FileStructure(byte[] array, String fileName) {
        this.array = array;
        this.fileName = fileName;
    }
}
