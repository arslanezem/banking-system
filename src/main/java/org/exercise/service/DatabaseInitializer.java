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
        try (Statement statement = getConnection().createStatement()) {
            createTables(statement);
            insertSampleData(statement);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void createTables(Statement statement) throws SQLException {
        // Create Client table
        statement.executeUpdate("DROP TABLE IF EXISTS CLIENT");
        statement.executeUpdate("CREATE TABLE CLIENT " +
                "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                "accountNumber INT, " +
                "firstName VARCHAR(255), " +
                "lastName VARCHAR(255), " +
                "age INT)");

        // Create Account table
        statement.executeUpdate("DROP TABLE IF EXISTS ACCOUNT");
        statement.executeUpdate("CREATE TABLE ACCOUNT " +
                "(accountNumber INT PRIMARY KEY, " +
                "balance INT)");

        // Create Transaction table
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

        // Inserting Accounts for corresponding clients
        statement.executeUpdate("INSERT INTO ACCOUNT (accountNumber, balance) VALUES (123, 1000)");
        statement.executeUpdate("INSERT INTO ACCOUNT (accountNumber, balance) VALUES (456, 500)");

        // Inserting Transactions for corresponding accounts
        statement.executeUpdate("INSERT INTO TRANSACTION (transactionType, amount, accountNumber) VALUES ('DEPOSIT', 500, 123)");
        statement.executeUpdate("INSERT INTO TRANSACTION (transactionType, amount, accountNumber) VALUES ('WITHDRAWAL', 200, 123)");
        //statement.executeUpdate("INSERT INTO TRANSACTION (transactionType, amount, accountNumber) VALUES ('DEPOSIT', 300, 456)");
    }
}
