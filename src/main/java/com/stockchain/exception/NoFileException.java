package com.stockchain.exception;

public class NoFileException extends Exception {
    private static final String m = "No file given !";

    public NoFileException() {
        super(m);
    }

    public NoFileException(String message) {
        super(m + "/n" + message);

    }
}
