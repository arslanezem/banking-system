package org.exercise.server;

import com.sun.net.httpserver.HttpServer;
import org.exercise.handlers.RequestHandler;
import org.exercise.service.DatabaseInitializer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * The main class for the Banking HTTP server application.
 * This class initializes and starts the HTTP server to handle banking-related requests.
 * The server listens on port 9090 and has a single endpoint at "/api/request".
 * The actual request handling logic is delegated to the RequestHandler class.
 */
public class BankingServer {

    /**
     * Main method to start the Banking HTTP server.
     *
     * @param args Command line arguments (not used).
     * @throws IOException If an I/O error occurs while starting the server.
     */
    public static void main(String[] args) throws Exception {

        // Initialize h2 database by creating and inserting into the following tables : Client, Transaction and Account
        DatabaseInitializer.initializeDatabase();

        // Create and start the server
        HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);
        server.createContext("/api/request", new RequestHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("Banking HTTP server started on port 9090");
    }
}
