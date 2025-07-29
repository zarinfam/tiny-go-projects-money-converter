package com.moneyconverter.money;

public interface RatesFetcher {
    ExchangeRate fetchExchangeRate(Currency source, Currency target) throws MoneyException;
}