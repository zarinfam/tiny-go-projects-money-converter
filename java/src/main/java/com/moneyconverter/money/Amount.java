package com.moneyconverter.money;

public record Amount(Decimal quantity, Currency currency) {
    private static final long MAX_DECIMAL = 1_000_000_000_000L;

    public static Amount of(Decimal quantity, Currency currency) throws MoneyException {
        if (quantity.getPrecision() > currency.precision()) {
            throw new MoneyException("quantity is too precise");
        } else if (quantity.getPrecision() < currency.precision()) {
            long multiplier = pow10((byte) (currency.precision() - quantity.getPrecision()));
            quantity.setSubunits(quantity.getSubunits() * multiplier);
            quantity.setPrecision(currency.precision());
        }
        if (quantity.getSubunits() > MAX_DECIMAL) {
            throw new MoneyException("quantity over 10^12 is too large");
        }
        return new Amount(quantity, currency);
    }

    @Override
    public String toString() {
        return "%s %s".formatted(quantity, currency);
    }

    private static long pow10(byte power) {
        return switch (power) {
            case 0 -> 1L;
            case 1 -> 10L;
            case 2 -> 100L;
            case 3 -> 1000L;
            case byte p when p > 3 -> (long) Math.pow(10, p);
            default -> throw new IllegalArgumentException("Negative power not supported");
        };
    }
}