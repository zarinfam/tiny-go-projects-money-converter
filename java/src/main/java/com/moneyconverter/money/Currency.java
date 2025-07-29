package com.moneyconverter.money;

public class Currency {
    private final String code;
    private final byte precision;

    private Currency(String code, byte precision) {
        this.code = code;
        this.precision = precision;
    }

    public static Currency parseCurrency(String code) throws MoneyException {
        if (code.length() != 3) {
            throw new MoneyException("invalid currency code");
        }

        return switch (code) {
            case "IRR" -> new Currency(code, (byte) 0);
            case "MGA", "MRU" -> new Currency(code, (byte) 1);
            case "CNY", "VND" -> new Currency(code, (byte) 1);
            case "BHD", "IQD", "KWD", "LYD", "OMR", "TND" -> new Currency(code, (byte) 3);
            default -> new Currency(code, (byte) 2);
        };
    }

    public String getCode() {
        return code;
    }

    public byte getPrecision() {
        return precision;
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Currency currency = (Currency) obj;
        return precision == currency.precision && code.equals(currency.code);
    }

    @Override
    public int hashCode() {
        return code.hashCode() * 31 + precision;
    }
}