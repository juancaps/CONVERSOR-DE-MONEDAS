package com.jc.conversor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;

public class GeneradorDeArchivo {

    public void guardarJson(Moneda moneda, String nombreArchivo) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        try (FileWriter escritura = new FileWriter(sanitizar(nombreArchivo))) {
            escritura.write(gson.toJson(moneda));
        }
    }

    private String sanitizar(String nombre) {
        String n = (nombre == null || nombre.isBlank()) ? "monedas.json" : nombre;
        return n.replaceAll("[\\/:*?\"<>|]", "_");
    }
}
