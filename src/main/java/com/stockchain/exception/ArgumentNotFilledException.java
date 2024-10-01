package com.stockchain.exception;

public class ArgumentNotFilledException extends Exception {
    public ArgumentNotFilledException() {
        super("One of the required arguments is not filled !");
    }
}
