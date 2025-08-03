package com.moneyconverter.money;

public class Convert {
    public static Amount convert(Amount amount, Currency to, RatesFetcher rates) throws MoneyException {
        ExchangeRate r = rates.fetchExchangeRate(amount.currency(), to);
        return applyExchangeRate(amount, to, r);
    }

    private static Amount applyExchangeRate(Amount a, Currency target, ExchangeRate rate) throws MoneyException {
        Decimal converted = a.quantity().multiply(rate.rate());

        byte targetPrecision = target.precision();
        byte convertedPrecision = converted.getPrecision();

        if (convertedPrecision > targetPrecision) {
            long divisor = pow10((byte) (convertedPrecision - targetPrecision));
            converted.setSubunits(converted.getSubunits() / divisor);
        } else if (convertedPrecision < targetPrecision) {
            long multiplier = pow10((byte) (targetPrecision - convertedPrecision));
            converted.setSubunits(converted.getSubunits() * multiplier);
        }
        
        converted.setPrecision(targetPrecision);

        return Amount.of(converted, target);
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