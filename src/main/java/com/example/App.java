
package com.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class App {

    // Calculator methods

    public static double add(double a, double b) {
        return a + b;
    }

    public static double subtract(double a, double b) {
        return a - b;
    }

    public static double multiply(double a, double b) {
        return a * b;
    }

    public static double divide(double a, double b) {
        if (b == 0) {
            throw new IllegalArgumentException("Cannot divide by zero");
        }
        return a / b;
    }

    // Send HTTP response

    private static void sendResponse(
            HttpExchange exchange,
            int statusCode,
            String response
    ) throws IOException {

        byte[] responseBytes =
                response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "text/plain; charset=UTF-8"
        );

        exchange.sendResponseHeaders(
                statusCode,
                responseBytes.length
        );

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    // Create HTTP server

    public static HttpServer createServer(int port) throws IOException {

        HttpServer server = HttpServer.create(
                new InetSocketAddress("0.0.0.0", port),
                0
        );

        // Health endpoint
        server.createContext("/health", exchange -> {
            sendResponse(
                    exchange,
                    200,
                    "Application is healthy!"
            );
        });

        // Home endpoint
        server.createContext("/", exchange -> {

            String response =
                    "================================\n" +
                    "     JAVA CI/CD CALCULATOR\n" +
                    "================================\n\n" +
                    "Addition: " + add(10, 5) + "\n" +
                    "Subtraction: " + subtract(10, 5) + "\n" +
                    "Multiplication: " + multiply(10, 5) + "\n" +
                    "Division: " + divide(10, 5) + "\n\n" +
                    "Application deployed successfully!\n";

            sendResponse(exchange, 200, response);
        });

        server.setExecutor(null);

        return server;
    }

    // Main method

    public static void main(String[] args) throws IOException {

        HttpServer server = createServer(8080);

        server.start();

        System.out.println(
                "Java Calculator Server started on port 8080"
        );
    }
}
