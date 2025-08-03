package com.moneyconverter.money;

public record Currency(String code, byte precision) {    
    public static Currency of(String code, byte precision) throws MoneyException {
        if (code == null || code.length() != 3) {
            throw new MoneyException("invalid currency code");
        }
        return new Currency(code, precision);
    }
    
    public static Currency parseCurrency(String code) throws MoneyException {
        if (code == null || code.length() != 3) {
            throw new MoneyException("invalid currency code");
        }

        return switch (code) {
            case "IRR" -> Currency.of(code, (byte) 0);
            case "MGA", "MRU", "CNY", "VND" -> Currency.of(code, (byte) 1);
            case "BHD", "IQD", "KWD", "LYD", "OMR", "TND" -> Currency.of(code, (byte) 3);
            case String s when s.matches("[A-Z]{3}") -> Currency.of(code, (byte) 2);
            default -> throw new MoneyException("invalid currency code");
        };
    }

    @Override
    public String toString() {
        return code;
    }
}