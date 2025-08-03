package com.moneyconverter.ecbank;

import com.moneyconverter.money.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Lightweight XML parser specifically for ECB exchange rate format.
 * Avoids heavy DOM parsing for better performance and smaller binary size.
 */
public class LightweightXmlParser {
    
    private static final Pattern CUBE_PATTERN = Pattern.compile(
        "<Cube\\s+currency=['\"]([A-Z]{3})['\"]\\s+rate=['\"]([0-9.]+)['\"]", 
        Pattern.CASE_INSENSITIVE
    );
    
    public static ExchangeRate readRateFromResponse(String sourceCurrency, String targetCurrency, String xmlContent) throws MoneyException, EcbException {
        if (sourceCurrency.equals(targetCurrency)) {
            return new ExchangeRate(Decimal.parseDecimal("1.0"));
        }
        
        if ("EUR".equals(sourceCurrency)) {
            return findDirectRate(targetCurrency, xmlContent);
        }
        
        if ("EUR".equals(targetCurrency)) {
            return findInverseRate(sourceCurrency, xmlContent);
        }
        
        // Cross-currency conversion via EUR
        ExchangeRate sourceToEur = findInverseRate(sourceCurrency, xmlContent);
        ExchangeRate eurToTarget = findDirectRate(targetCurrency, xmlContent);
        
        Decimal crossRate = eurToTarget.rate().multiply(sourceToEur.rate());
        
        return new ExchangeRate(crossRate);
    }
    
    private static ExchangeRate findDirectRate(String targetCurrency, String xmlContent) throws MoneyException, EcbException {
        Matcher matcher = CUBE_PATTERN.matcher(xmlContent);
        
        while (matcher.find()) {
            String currency = matcher.group(1);
            if (currency.equals(targetCurrency)) {
                String rateStr = matcher.group(2);
                return new ExchangeRate(Decimal.parseDecimal(rateStr));
            }
        }
        
        throw new EcbException("Exchange rate not found for currency: " + targetCurrency);
    }
    
    private static ExchangeRate findInverseRate(String sourceCurrency, String xmlContent) throws MoneyException, EcbException {
        Matcher matcher = CUBE_PATTERN.matcher(xmlContent);
        
        while (matcher.find()) {
            String currency = matcher.group(1);
            if (currency.equals(sourceCurrency)) {
                String rateStr = matcher.group(2);
                double rate = Double.parseDouble(rateStr);
                double inverseRate = 1.0 / rate;
                
                return new ExchangeRate(Decimal.parseDecimal(String.format("%.10f", inverseRate)));
            }
        }
        
        throw new EcbException("Exchange rate not found for currency: " + sourceCurrency);
    }
}
