package com.moneyconverter.ecbank;

import com.moneyconverter.money.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class Client implements RatesFetcher {
    private final HttpClient client;
    private static final String EUROXREF_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";

    public Client(Duration timeout) {
        this.client = HttpClient.newBuilder()
                .connectTimeout(timeout)
                .build();
    }

    @Override
    public ExchangeRate fetchExchangeRate(Currency source, Currency target) throws MoneyException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(EUROXREF_URL))
                    .timeout(Duration.ofSeconds(30))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            checkStatusCode(response.statusCode());

            return XmlParser.readRateFromResponse(source.getCode(), target.getCode(), response.body());

        } catch (IOException e) {
            throw new MoneyException("error calling server: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new MoneyException("request interrupted: " + e.getMessage(), e);
        } catch (EcbException e) {
            throw new MoneyException(e.getMessage(), e);
        }
    }

    private void checkStatusCode(int statusCode) throws EcbException {
        if (statusCode == 200) {
            return;
        }

        int statusClass = statusCode / 100;
        switch (statusClass) {
            case 4 -> throw new EcbException("client side error when contacting ECB: " + statusCode);
            case 5 -> throw new EcbException("server side error when contacting ECB: " + statusCode);
            default -> throw new EcbException("unknown status code contacting ECB: " + statusCode);
        }
    }
}