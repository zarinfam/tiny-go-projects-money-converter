package com.moneyconverter.ecbank;

public class EcbException extends Exception {
    public EcbException(String message) {
        super(message);
    }

    public EcbException(String message, Throwable cause) {
        super(message, cause);
    }
}