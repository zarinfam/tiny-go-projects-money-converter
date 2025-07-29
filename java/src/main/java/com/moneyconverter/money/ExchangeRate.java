package com.moneyconverter.money;

public class ExchangeRate {
    private final Decimal rate;

    public ExchangeRate(Decimal rate) {
        this.rate = rate;
    }

    public Decimal getRate() {
        return rate;
    }
}

