import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.text.DecimalFormat;
import java.util.Scanner;

public class Principal {
    public static void main(String[] args) {
        Scanner lectura = new Scanner(System.in);

        System.out.println("¡Bienvenido al Conversor de monedas!");

        // Selección de moneda de origen
        System.out.println("Ingresa el número de la moneda de origen: ");
        System.out.println("1. Peso Mexicano (MXN)");
        System.out.println("2. Dólar Estadounidense (USD)");
        System.out.println("3. Euro (EUR)");
        System.out.println("4. Peso Colombiano (COP)");
        System.out.println("5. Peso Cubano (CUP)");

        int monedaOrigen = lectura.nextInt();

        String codigoMonedaOrigen = null;
        codigoMonedaOrigen = obtenerCodigoMoneda(monedaOrigen);
        System.out.println("Seleccionaste: " + codigoMonedaOrigen );

        // Selección de moneda de destino
        System.out.println("Ingresa el número de la moneda a la que deseas convertir: ");
        System.out.println("1. Peso Mexicano (MXN)");
        System.out.println("2. Dólar Estadounidense (USD)");
        System.out.println("3. Euro (EUR)");
        System.out.println("4. Peso Colombiano (COP)");
        System.out.println("5. Peso Cubano (CUP)");

        int monedaDestino = lectura.nextInt();

        // Validar selección de monedas
        String codigoMonedaDestino = obtenerCodigoMoneda(monedaDestino);
        System.out.println("Selccionaste: " + codigoMonedaDestino);

        // Solicitud del monto a convertir
        System.out.println("Ingresa el monto que deseas convertir: ");
        double monto = lectura.nextDouble();



        if (codigoMonedaOrigen == null || codigoMonedaDestino == null) {
            System.out.println("Error: Seleccionaste una moneda inválida.");
            return;
        }

        // Obtener las tasas de cambio desde la API
        try {
            String apiKey = "https://v6.exchangerate-api.com/v6/10331416abe5450cd145350c/latest/USD";
            String direccion = "https://v6.exchangerate-api.com/v6/10331416abe5450cd145350c/latest/USD";

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(direccion))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String json = response.body();

            Gson gson = new GsonBuilder()
                    .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                    .create();

            ExchangeRatesResponse exchangeRates = gson.fromJson(json, ExchangeRatesResponse.class);

            // Verificar si la respuesta es exitosa
            if (!"success".equalsIgnoreCase(exchangeRates.getResult())) {
                System.out.println("Error: No se pudo obtener la información de tasas de cambio.");
                return;
            }

            // Obtener las tasas de cambio para las monedas seleccionadas
            Double tasaOrigenUSD = exchangeRates.getConversion_rates().get(codigoMonedaOrigen);
            Double tasaDestinoUSD = exchangeRates.getConversion_rates().get(codigoMonedaDestino);

            if (tasaOrigenUSD == null || tasaDestinoUSD == null) {
                System.out.println("Error: La tasa de cambio no está disponible para las monedas seleccionadas.");
                return;
            }

            // Calcular la tasa de cambio entre monedaOrigen y monedaDestino
            double tasaCambio = tasaDestinoUSD / tasaOrigenUSD;

            // Calcular el monto convertido
            double montoConvertido = monto * tasaCambio;

            // Formatear el resultado
            DecimalFormat df = new DecimalFormat("#.##");
            System.out.println("El monto convertido de " + monto + " " + codigoMonedaOrigen + " a " + codigoMonedaDestino + " es: " + df.format(montoConvertido));

        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.out.println("Ocurrió un error al comunicarse con la API: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    // Método para obtener el código de moneda
    public static String obtenerCodigoMoneda(int seleccion) {
        switch (seleccion) {
            case 1:
                return "MXN";
            case 2:
                return "USD";
            case 3:
                return "EUR";
            case 4:
                return "COP";
            case 5:
                return "CUP";
            default:
                return null;
        }


    }
}