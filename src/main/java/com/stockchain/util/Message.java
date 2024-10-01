package com.stockchain.util;

import java.io.FileOutputStream;
import java.io.Serializable;

public class Message implements Serializable {

    private static final long serialVersionUID = 632055075746593756L;
    public String message;
    public String instruction;
    public FileStructure fileStructure;

    public Message(FileStructure fileStructure, String message, String instruction) {
        this.fileStructure = fileStructure;
        this.message = message;
        this.instruction = instruction;

    }

}
