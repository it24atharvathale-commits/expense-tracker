package com.demo;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class ExpenseServer {
    /**
     * Starts the application server and returns it so callers (including tests)
     * can choose a free port and stop the server when finished.
     */
    public static HttpServer start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress("0.0.0.0", port), 0);
        server.createContext("/", exchange -> {
            try {
                if (!"/".equals(exchange.getRequestURI().getPath())) {
                    send(exchange, 404, "Not found", "text/plain; charset=UTF-8");
                    return;
                }
                try (InputStream input = ExpenseServer.class.getResourceAsStream("/web/index.html")) {
                    if (input == null) {
                        send(exchange, 500, "Page resource not found", "text/plain; charset=UTF-8");
                        return;
                    }
                    send(exchange, 200, input.readAllBytes(), "text/html; charset=UTF-8");
                }
            } finally {
                exchange.close();
            }
        });
        server.createContext("/health", exchange -> {
            try {
                send(exchange, 200, "OK".getBytes(StandardCharsets.UTF_8), "text/plain; charset=UTF-8");
            } finally {
                exchange.close();
            }
        });
        server.start();
        System.out.println("Expense Calculator running on port " + server.getAddress().getPort());
        return server;
    }

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        start(port);
    }

    private static void send(com.sun.net.httpserver.HttpExchange ex, int status, String body, String type) throws IOException {
        send(ex, status, body.getBytes(StandardCharsets.UTF_8), type);
    }

    private static void send(com.sun.net.httpserver.HttpExchange ex, int status, byte[] body, String type) throws IOException {
        ex.getResponseHeaders().set("Content-Type", type);
        ex.sendResponseHeaders(status, body.length);
        ex.getResponseBody().write(body);
    }
}
