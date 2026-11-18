
package com.example;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.sun.net.httpserver.HttpServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

class AppTest {

    private static HttpServer server;
    private static HttpClient client;
    private static int port;

    // Start test server before all tests

    @BeforeAll
    static void startServer() throws Exception {

        server = App.createServer(0);

        server.start();

        port = server.getAddress().getPort();

        client = HttpClient.newHttpClient();
    }

    // Stop test server after all tests

    @AfterAll
    static void stopServer() {

        if (server != null) {
            server.stop(0);
        }
    }

    // Calculator tests

    @Test
    void testAddition() {
        assertEquals(15.0, App.add(10, 5));
    }

    @Test
    void testSubtraction() {
        assertEquals(5.0, App.subtract(10, 5));
    }

    @Test
    void testMultiplication() {
        assertEquals(50.0, App.multiply(10, 5));
    }

    @Test
    void testDivision() {
        assertEquals(2.0, App.divide(10, 5));
    }

    @Test
    void testDivisionByZero() {

        assertThrows(
                IllegalArgumentException.class,
                () -> App.divide(10, 0)
        );
    }

    // HTTP endpoint tests

    @Test
    void testHealthEndpoint() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "http://localhost:" + port + "/health"
                ))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());

        assertEquals(
                "Application is healthy!",
                response.body()
        );
    }

    @Test
    void testHomeEndpoint() throws Exception {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(
                        "http://localhost:" + port + "/"
                ))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        assertEquals(200, response.statusCode());

        assertTrue(response.body().contains("JAVA CI/CD CALCULATOR"));
        assertTrue(response.body().contains("Addition: 15.0"));
        assertTrue(response.body().contains("Subtraction: 5.0"));
        assertTrue(response.body().contains("Multiplication: 50.0"));
        assertTrue(response.body().contains("Division: 2.0"));
        assertTrue(response.body().contains(
                "Application deployed successfully!"
        ));
    }
}
