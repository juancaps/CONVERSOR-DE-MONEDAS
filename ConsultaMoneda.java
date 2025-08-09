package com.jc.conversor;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ConsultaMoneda {

    private static final String API_KEY = System.getenv().getOrDefault(
            "EXCHANGE_API_KEY",
            "cbb227a4175e8e65b60aeab0"
    );

    private static final String BASE_URL = "https://v6.exchangerate-api.com/v6/";
    private final HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private final Gson gson = new Gson();

    public Moneda buscaMoneda(String base) throws IOException, InterruptedException {
        String baseCode = base.toUpperCase();
        String url = BASE_URL + API_KEY + "/latest/" + baseCode;

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .GET()
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/json")
                .header("User-Agent", "ConversorMonedas/1.0")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("HTTP " + response.statusCode() + " al consultar tasas.");
        }

        Moneda dto = gson.fromJson(response.body(), Moneda.class);

        if (dto == null || dto.result() == null || !"success".equalsIgnoreCase(dto.result())) {
            throw new RuntimeException("No encontré tasas para la base " + baseCode);
        }

        return dto;
    }
}
