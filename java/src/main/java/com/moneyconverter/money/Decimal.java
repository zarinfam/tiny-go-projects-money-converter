package com.moneyconverter.money;

public class Decimal {
    private long subunits;
    private byte precision;

    private static final long MAX_DECIMAL = 1_000_000_000_000L; // 10^12

    public Decimal(long subunits, byte precision) {
        this.subunits = subunits;
        this.precision = precision;
    }

    public static Decimal parseDecimal(String value) throws MoneyException {
        String[] parts = value.split("\\.", 2);
        String intPart = parts[0];
        String fracPart = parts.length > 1 ? parts[1] : "";

        try {
            long subunits = Long.parseLong(intPart + fracPart);
            
            if (subunits > MAX_DECIMAL) {
                throw new MoneyException("quantity over 10^12 is too large");
            }

            byte precision = (byte) fracPart.length();
            Decimal decimal = new Decimal(subunits, precision);
            decimal.simplify();
            
            return decimal;
        } catch (NumberFormatException e) {
            throw new MoneyException("unable to convert the decimal", e);
        }
    }

    @Override
    public String toString() {
        if (precision == 0) {
            return String.valueOf(subunits);
        }

        long centsPerUnit = pow10(precision);
        long frac = subunits % centsPerUnit;
        long integer = subunits / centsPerUnit;

        String format = "%d.%0" + precision + "d";
        return String.format(format, integer, frac);
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

    private void simplify() {
        while (subunits % 10 == 0 && precision > 0) {
            precision--;
            subunits /= 10;
        }
    }

    public long getSubunits() {
        return subunits;
    }

    public byte getPrecision() {
        return precision;
    }

    public void setSubunits(long subunits) {
        this.subunits = subunits;
    }

    public void setPrecision(byte precision) {
        this.precision = precision;
    }

    public Decimal multiply(Decimal other) {
        Decimal result = new Decimal(
            this.subunits * other.subunits,
            (byte) (this.precision + other.precision)
        );
        result.simplify();
        return result;
    }
}