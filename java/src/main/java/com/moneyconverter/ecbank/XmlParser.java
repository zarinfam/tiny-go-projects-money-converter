package com.moneyconverter.ecbank;

import com.moneyconverter.money.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

public class XmlParser {
    private static final String BASE_CURRENCY_CODE = "EUR";

    public static ExchangeRate readRateFromResponse(String source, String target, String responseBody) 
            throws EcbException, MoneyException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new ByteArrayInputStream(responseBody.getBytes()));

            NodeList cubeNodes = doc.getElementsByTagName("Cube");
            Map<String, Double> rates = new HashMap<>();

            for (int i = 0; i < cubeNodes.getLength(); i++) {
                Node node = cubeNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String currency = element.getAttribute("currency");
                    String rateStr = element.getAttribute("rate");
                    
                    if (!currency.isEmpty() && !rateStr.isEmpty()) {
                        rates.put(currency, Double.parseDouble(rateStr));
                    }
                }
            }

            rates.put(BASE_CURRENCY_CODE, 1.0);

            return calculateExchangeRate(source, target, rates);

        } catch (Exception e) {
            throw new EcbException("unexpected response format: " + e.getMessage(), e);
        }
    }

    private static ExchangeRate calculateExchangeRate(String source, String target, Map<String, Double> rates) 
            throws EcbException, MoneyException {
        if (source.equals(target)) {
            Decimal one = Decimal.parseDecimal("1");
            return new ExchangeRate(one);
        }

        Double sourceFactor = rates.get(source);
        if (sourceFactor == null) {
            throw new EcbException("failed to find the source currency " + source);
        }

        Double targetFactor = rates.get(target);
        if (targetFactor == null) {
            throw new EcbException("failed to find target currency " + target);
        }

        double rate = targetFactor / sourceFactor;
        Decimal rateDecimal = Decimal.parseDecimal(String.format("%.10f", rate));
        return new ExchangeRate(rateDecimal);
    }
}