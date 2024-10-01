package com.stockchain.exception;

public class NotExpectedAnswerException extends Exception {
    public NotExpectedAnswerException() {
        super();
    }

    public NotExpectedAnswerException(String message) {
        super(message);
    }
}
