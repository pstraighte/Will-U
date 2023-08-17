package com.beteam.willu.exception;

public class BlackListedTokenException extends RuntimeException {
    public BlackListedTokenException(String message) {
        super(message);
    }
}
