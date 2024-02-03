package org.exercise.service;

import org.exercise.model.Client;

import java.sql.*;

public class DatabaseInitializer {

    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return connection;
    }


    public static Connection initializeDatabase() {
        try (
                // Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
             Statement statement = getConnection().createStatement()) {

            createTables(statement);
            insertSampleData(statement);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void createTables(Statement statement) throws SQLException {
        // Crée la table Client
        statement.executeUpdate("DROP TABLE IF EXISTS CLIENT");
        statement.executeUpdate("CREATE TABLE CLIENT " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                "accountNumber INT, " +
                "firstName VARCHAR(255), " +
                "lastName VARCHAR(255), " +
                "age INT)"); /*, " +
                "FOREIGN KEY (accountNumber) REFERENCES Account(accountNumber))");*/

        // Crée la table Account
        statement.executeUpdate("DROP TABLE IF EXISTS ACCOUNT");
        statement.executeUpdate("CREATE TABLE ACCOUNT " +
                "(accountNumber INT PRIMARY KEY, " +
                "balance INT)"); /*, " +
                "clientId INT, " +
                "FOREIGN KEY (clientId) REFERENCES Client(id))");*/

        // Crée la table Transaction
        statement.executeUpdate("DROP TABLE IF EXISTS TRANSACTION");
        statement.executeUpdate("CREATE TABLE TRANSACTION " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                "transactionType VARCHAR(255), " +
                "amount INT, " +
                "accountNumber INT, " +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (accountNumber) REFERENCES ACCOUNT(accountNumber))");
    }

    private static void insertSampleData(Statement statement) throws SQLException {
        // Insertion de clients
        statement.executeUpdate("INSERT INTO CLIENT " +
                "(accountNumber, firstName, lastName, age) VALUES " +
                "(123, 'John', 'Doe', 30)");
        statement.executeUpdate("INSERT INTO CLIENT " +
                "(accountNumber, firstName, lastName, age) VALUES " +
                "(456, 'Alice', 'Smith', 25)");

        // Récupération des IDs des clients insérés
        ResultSet clientResultSet = statement.executeQuery("SELECT id FROM CLIENT WHERE firstName = 'John'");
        clientResultSet.next();
        int johnClientId = clientResultSet.getInt("id");

        clientResultSet = statement.executeQuery("SELECT id FROM CLIENT WHERE firstName = 'Alice'");
        clientResultSet.next();
        int aliceClientId = clientResultSet.getInt("id");

        // Insertion de comptes associés aux clients
        statement.executeUpdate("INSERT INTO ACCOUNT (accountNumber, balance) VALUES (123, 1000)");
        statement.executeUpdate("INSERT INTO ACCOUNT (accountNumber, balance) VALUES (456, 500)");

        // Insertion de transactions associées aux comptes
        statement.executeUpdate("INSERT INTO TRANSACTION (transactionType, amount, accountNumber) VALUES ('DEPOSIT', 500, 123)");
        statement.executeUpdate("INSERT INTO TRANSACTION (transactionType, amount, accountNumber) VALUES ('WITHDRAWAL', 200, 123)");
        statement.executeUpdate("INSERT INTO TRANSACTION (transactionType, amount, accountNumber) VALUES ('DEPOSIT', 300, 456)");
    }



/*
    public static Client getClientByAccountNumber(int accountNumber) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password")) {
            String query = "SELECT c.* FROM Client c JOIN Account a ON c.id = a.clientId WHERE a.accountNumber = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, accountNumber);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String firstName = resultSet.getString("firstName");
                        String lastName = resultSet.getString("lastName");
                        int age = resultSet.getInt("age");

                        return new Client(id, firstName, lastName, age);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null; // Retourne null si le client n'est pas trouvé
    }*/





}
