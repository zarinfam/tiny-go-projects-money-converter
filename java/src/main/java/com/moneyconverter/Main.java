package com.moneyconverter;

import com.moneyconverter.money.*;
import com.moneyconverter.ecbank.Client;
import java.time.Duration;

public class Main {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: money-converter -from <currency> [-to <currency>] <amount>");
            System.err.println("Example: money-converter -from USD -to EUR 100");
            System.exit(1);
        }

        String fromCurrency = null;
        String toCurrency = "EUR";
        String amountStr = null;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-from":
                    if (i + 1 < args.length) {
                        fromCurrency = args[++i];
                    }
                    break;
                case "-to":
                    if (i + 1 < args.length) {
                        toCurrency = args[++i];
                    }
                    break;
                default:
                    if (!args[i].startsWith("-")) {
                        amountStr = args[i];
                    }
                    break;
            }
        }

        if (fromCurrency == null) {
            System.err.println("Source currency (-from) is required");
            System.exit(1);
        }

        if (amountStr == null) {
            System.err.println("Amount to convert is required");
            System.exit(1);
        }

        try {
            Currency toCurrencyObj = Currency.parseCurrency(toCurrency);
            Amount amount = parseAmount(amountStr, fromCurrency);
            
            Client rates = new Client(Duration.ofSeconds(30));
            
            Amount convertedAmount = Convert.convert(amount, toCurrencyObj, rates);
            
            System.out.printf("%s = %s%n", amount, convertedAmount);
            
        } catch (MoneyException e) {
            System.err.println("Error: " + e.getMessage());
            System.exit(1);
        }
    }

    private static Amount parseAmount(String value, String fromCurrency) throws MoneyException {
        Currency fromCurrencyObj = Currency.parseCurrency(fromCurrency);
        Decimal quantity = Decimal.parseDecimal(value);
        return new Amount(quantity, fromCurrencyObj);
    }
}