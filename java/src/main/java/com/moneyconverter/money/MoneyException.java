package com.moneyconverter.money;

public class MoneyException extends Exception {
    public MoneyException(String message) {
        super(message);
    }

    public MoneyException(String message, Throwable cause) {
        super(message, cause);
    }
}