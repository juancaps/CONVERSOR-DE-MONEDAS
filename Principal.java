package com.jc.conversor;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Locale;
import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {
        Scanner teclado = new Scanner(System.in);
        ConsultaMoneda consulta = new ConsultaMoneda();
        GeneradorDeArchivo generador = new GeneradorDeArchivo();
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(new Locale("es", "CO"));
        df.setMaximumFractionDigits(4);
        df.setMinimumFractionDigits(2);

        boolean continuar = true;

        while (continuar) {
            System.out.println("************************************************");
            System.out.println("  =) Sea bienvenid@ al Conversor de Moneda =P");
            System.out.println();
            System.out.println("  1) Dólar >> Peso argentino");
            System.out.println("  2) Peso argentino >> Dólar");
            System.out.println("  3) Dólar >> Real brasileño");
            System.out.println("  4) Real brasileño >> Dólar");
            System.out.println("  5) Dólar >> Peso colombiano");
            System.out.println("  6) Peso colombiano >> Dólar");
            System.out.println("  7) Salir");
            System.out.println("************************************************");
            System.out.print("Elija una opción válida: ");

            String opcion = teclado.nextLine().trim();

            try {
                switch (opcion) {
                    case "1" -> convertir(consulta, generador, df, teclado, "USD", "ARS");
                    case "2" -> convertir(consulta, generador, df, teclado, "ARS", "USD");
                    case "3" -> convertir(consulta, generador, df, teclado, "USD", "BRL");
                    case "4" -> convertir(consulta, generador, df, teclado, "BRL", "USD");
                    case "5" -> convertir(consulta, generador, df, teclado, "USD", "COP");
                    case "6" -> convertir(consulta, generador, df, teclado, "COP", "USD");
                    case "7" -> {
                        System.out.println("Finalizando la aplicación.");
                        continuar = false;
                    }
                    default -> System.out.println("Opción inválida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un monto numérico válido.");
            } catch (RuntimeException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error de E/S: " + e.getMessage());
            } catch (InterruptedException e) {
                System.out.println("La operación fue interrumpida.");
                Thread.currentThread().interrupt();
            }

            System.out.println();
        }
    }

    private static void convertir(ConsultaMoneda consulta,
                                  GeneradorDeArchivo generador,
                                  DecimalFormat df,
                                  Scanner teclado,
                                  String base,
                                  String destino)
            throws IOException, InterruptedException {
        System.out.print("Ingrese el monto en " + base + ": ");
        String montoStr = teclado.nextLine().trim().replace(",", ".");
        BigDecimal monto = new BigDecimal(montoStr);

        Moneda tasas = consulta.buscaMoneda(base); // /latest/{base}
        Double rate = tasas.conversion_rates().get(destino);
        if (rate == null) {
            throw new RuntimeException("No hay tasa disponible de " + base + " a " + destino);
        }

        BigDecimal resultado = monto.multiply(new BigDecimal(rate.toString()));

        System.out.println("Resultado: " + base + " " + df.format(monto) +
                " = " + destino + " " + df.format(resultado));

        // Guardamos un JSON con la consulta
        generador.guardarJson(tasas, base + "_latest.json");
    }
}
