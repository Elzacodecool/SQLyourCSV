package com.codecool.exception;

public class WrongQueryFormatException extends RuntimeException {
    public WrongQueryFormatException(String messege) {
        super(messege);
    }
}
