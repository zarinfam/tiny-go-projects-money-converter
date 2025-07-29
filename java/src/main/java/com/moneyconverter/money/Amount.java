package com.moneyconverter.money;

public class Amount {
    private final Decimal quantity;
    private final Currency currency;

    private static final long MAX_DECIMAL = 1_000_000_000_000L;

    public Amount(Decimal quantity, Currency currency) throws MoneyException {
        if (quantity.getPrecision() > currency.getPrecision()) {
            throw new MoneyException("quantity is too precise");
        } else if (quantity.getPrecision() < currency.getPrecision()) {
            long multiplier = pow10((byte) (currency.getPrecision() - quantity.getPrecision()));
            quantity.setSubunits(quantity.getSubunits() * multiplier);
            quantity.setPrecision(currency.getPrecision());
        }

        this.quantity = quantity;
        this.currency = currency;
    }

    public void validate() throws MoneyException {
        if (quantity.getSubunits() > MAX_DECIMAL) {
            throw new MoneyException("quantity over 10^12 is too large");
        }
        if (quantity.getPrecision() > currency.getPrecision()) {
            throw new MoneyException("quantity is too precise");
        }
    }

    @Override
    public String toString() {
        return quantity.toString() + " " + currency.toString();
    }

    public Decimal getQuantity() {
        return quantity;
    }

    public Currency getCurrency() {
        return currency;
    }

    private static long pow10(byte power) {
        return switch (power) {
            case 0 -> 1L;
            case 1 -> 10L;
            case 2 -> 100L;
            case 3 -> 1000L;
            default -> (long) Math.pow(10, power);
        };
    }
}