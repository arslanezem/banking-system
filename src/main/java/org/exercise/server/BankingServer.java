package org.exercise.server;

import com.sun.net.httpserver.HttpServer;
import org.exercise.handlers.RequestHandler;
import org.exercise.service.DatabaseInitializer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.*;

import static org.exercise.service.DatabaseInitializer.getConnection;
import static org.exercise.service.TransactionService.getAccountBalanceByAccountNumber;

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
        Connection connection = DatabaseInitializer.initializeDatabase();

        // Create and start the server
        HttpServer server = HttpServer.create(new InetSocketAddress(9090), 0);
        server.createContext("/api/request", new RequestHandler());
        server.setExecutor(null);
        server.start();

        System.out.println("Banking HTTP server started on port 9090");

        // Display trasactions of all clients
        // displayTransactionsForClients(connection);

    }


    private static void displayTransactionsForClients(Connection con) {
        try (
                //Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
             Statement statement = con.createStatement()) {

            ResultSet clientResultSet = statement.executeQuery("SELECT * FROM CLIENT");

            while (clientResultSet.next()) {
                int accountNumber = clientResultSet.getInt("accountNumber");
                String firstName = clientResultSet.getString("firstName");
                String lastName = clientResultSet.getString("lastName");

                System.out.println("Transactions for client: " + firstName + " " + lastName);

                // Récupère les transactions pour le client spécifié
                ResultSet transactionResultSet = con.createStatement().executeQuery(
                        "SELECT * FROM TRANSACTION WHERE accountNumber IN (SELECT accountNumber FROM ACCOUNT WHERE accountNumber = " + accountNumber + ")");

                // Affiche les transactions
                while (transactionResultSet.next()) {
                    int transactionId = transactionResultSet.getInt("id");
                    String transactionType = transactionResultSet.getString("transactionType");
                    double amount = transactionResultSet.getDouble("amount");

                    System.out.println("  Transaction ID: " + transactionId);
                    System.out.println("  Transaction Type: " + transactionType);
                    System.out.println("  Amount: " + amount);
                    System.out.println("  --------------");
                }

                System.out.println();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }




    }





}




























































//        Client c1 = new Client(1, "arslane", "zem", 24);
//        Client c2 = new Client(2, "jack", "sum", 38);
//        ArrayList<Client> clients = new ArrayList<>();
//        clients.add(c1);
//        clients.add(c2);
//
//        Account a1 = new Account(c1.getAccountNumber());
//        Account a2 = new Account(c2.getAccountNumber());
//
//        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
//             Statement statement = connection.createStatement()) {
//            // Crée la table Client
//            statement.executeUpdate("CREATE TABLE Client (id INT AUTO_INCREMENT PRIMARY KEY, firstName VARCHAR(255), lastName VARCHAR(255),age INT)");
//
//            // Crée la table Account
//            statement.executeUpdate("CREATE TABLE Account (id INT AUTO_INCREMENT PRIMARY KEY, accountNumber VARCHAR(255), balance INT, clientId INT, FOREIGN KEY (clientId) REFERENCES Client(id))");
//
//            // Crée la table Transaction
//            statement.executeUpdate("CREATE TABLE Transaction (id INT AUTO_INCREMENT PRIMARY KEY, transactionType VARCHAR(255), amount DOUBLE, accountId INT, FOREIGN KEY (accountId) REFERENCES Account(id))");
//
//
//            statement.executeUpdate("INSERT INTO Client (firstName, lastName, age) VALUES ('John', 'Doe', 30)");
//            statement.executeUpdate("INSERT INTO Client (firstName, lastName, age) VALUES ('Alice', 'Smith', 25)");
//
//            ResultSet clientResultSet = statement.executeQuery("SELECT id FROM Client WHERE firstName = 'John'");
//            clientResultSet.next();
//            int johnClientId = clientResultSet.getInt("id");
//
//            clientResultSet = statement.executeQuery("SELECT id FROM Client WHERE firstName = 'Alice'");
//            clientResultSet.next();
//            int aliceClientId = clientResultSet.getInt("id");
//
//            // Insertion de comptes associés aux clients
//            statement.executeUpdate("INSERT INTO Account (accountNumber, balance, clientId) VALUES (123, 1000.0, " + johnClientId + ")");
//            statement.executeUpdate("INSERT INTO Account (accountNumber, balance, clientId) VALUES (456, 500.0, " + aliceClientId + ")");
//
//            // Insertion de transactions associées aux comptes
//            statement.executeUpdate("INSERT INTO Transaction (transactionType, amount, accountId) VALUES ('DEPOSIT', 500.0, (SELECT id FROM Account WHERE accountNumber = 123))");
//            statement.executeUpdate("INSERT INTO Transaction (transactionType, amount, accountId) VALUES ('WITHDRAWAL', 200.0, (SELECT id FROM Account WHERE accountNumber = 123))");
//            statement.executeUpdate("INSERT INTO Transaction (transactionType, amount, accountId) VALUES ('DEPOSIT', 300.0, (SELECT id FROM Account WHERE accountNumber = 456))");
//
//
//
//
//
//            // Supposons que tu veuilles récupérer l'historique des transactions pour le compte avec accountNumber 123
//            ResultSet accountResultSet = statement.executeQuery("SELECT id FROM Account WHERE accountNumber = 123");
//            accountResultSet.next();
//            int accountId = accountResultSet.getInt("id");
//
//            // Sélectionne l'historique des transactions pour le compte spécifié
//            ResultSet transactionResultSet = statement.executeQuery("SELECT * FROM Transaction WHERE accountId = " + accountId);
//
//            // Parcours et affiche les résultats
//            while (transactionResultSet.next()) {
//                int transactionId = transactionResultSet.getInt("id");
//                String transactionType = transactionResultSet.getString("transactionType");
//                double amount = transactionResultSet.getDouble("amount");
//
//                // Fais quelque chose avec les données récupérées, par exemple, affiche-les
//                System.out.println("Transaction ID: " + transactionId);
//                System.out.println("Transaction Type: " + transactionType);
//                System.out.println("Amount: " + amount);
//                System.out.println("--------------");
//            }
//
//
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }