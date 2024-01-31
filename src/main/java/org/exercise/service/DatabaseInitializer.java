package org.exercise.service;

import java.sql.*;

public class DatabaseInitializer {
    public static void initializeDatabase() {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
             Statement statement = connection.createStatement()) {
            createTables(statement);
            insertSampleData(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void createTables(Statement statement) throws SQLException {
        // Crée la table Client
        statement.executeUpdate("CREATE TABLE Client " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                "firstName VARCHAR(255), " +
                "lastName VARCHAR(255), " +
                "age INT)");

        // Crée la table Account
        statement.executeUpdate("CREATE TABLE Account " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                "accountNumber VARCHAR(255), " +
                "balance INT, clientId INT, " +
                "FOREIGN KEY (clientId) REFERENCES Client(id))");

        // Crée la table Transaction
        statement.executeUpdate("CREATE TABLE Transaction " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                "transactionType VARCHAR(255), " +
                "amount DOUBLE, " +
                "accountId INT, " +
                "FOREIGN KEY (accountId) REFERENCES Account(id))");
    }

    private static void insertSampleData(Statement statement) throws SQLException {
        // Insertion de clients
        statement.executeUpdate("INSERT INTO Client " +
                "(firstName, lastName, age) VALUES " +
                "('John', 'Doe', 30)");
        statement.executeUpdate("INSERT INTO Client " +
                "(firstName, lastName, age) VALUES " +
                "('Alice', 'Smith', 25)");

        // Récupération des IDs des clients insérés
        ResultSet clientResultSet = statement.executeQuery("SELECT id FROM Client WHERE firstName = 'John'");
        clientResultSet.next();
        int johnClientId = clientResultSet.getInt("id");

        clientResultSet = statement.executeQuery("SELECT id FROM Client WHERE firstName = 'Alice'");
        clientResultSet.next();
        int aliceClientId = clientResultSet.getInt("id");

        // Insertion de comptes associés aux clients
        statement.executeUpdate("INSERT INTO Account (accountNumber, balance, clientId) VALUES (123, 1000.0, " + johnClientId + ")");
        statement.executeUpdate("INSERT INTO Account (accountNumber, balance, clientId) VALUES (456, 500.0, " + aliceClientId + ")");

        // Insertion de transactions associées aux comptes
        statement.executeUpdate("INSERT INTO Transaction (transactionType, amount, accountId) VALUES ('DEPOSIT', 500.0, (SELECT id FROM Account WHERE accountNumber = 123))");
        statement.executeUpdate("INSERT INTO Transaction (transactionType, amount, accountId) VALUES ('WITHDRAWAL', 200.0, (SELECT id FROM Account WHERE accountNumber = 123))");
        statement.executeUpdate("INSERT INTO Transaction (transactionType, amount, accountId) VALUES ('DEPOSIT', 300.0, (SELECT id FROM Account WHERE accountNumber = 456))");
    }






}
