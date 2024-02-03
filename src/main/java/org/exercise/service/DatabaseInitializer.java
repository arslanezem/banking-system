package org.exercise.service;

import org.exercise.model.Client;

import java.sql.*;

/**
 * The DatabaseInitializer class is responsible for initializing the in-memory H2 database
 * by creating tables and inserting sample data.
 */
public class DatabaseInitializer {
    private static Connection connection;

    /**
     * Gets a connection to the in-memory H2 database.
     * If the connection is not already established, it will create a new one.
     *
     * @return The database connection.
     */
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

    /**
     * Initializes the in-memory H2 database by creating tables and inserting sample data.
     *
     * @return The initialized database connection.
     * @throws SQLException If an SQL exception occurs during database initialization.
     */
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

    /**
     * Creates three tables in the database: CLIENT, ACCOUNT, and TRANSACTION.
     * The CLIENT table stores client information, ACCOUNT table stores account details,
     * and TRANSACTION table records transaction history.
     *
     * @param statement The SQL statement to execute.
     * @throws SQLException If an SQL exception occurs during table creation.
     */
    private static void createTables(Statement statement) throws SQLException {
        try {
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
                    "newBalance INT, " +
                    "accountNumber INT, " +
                    "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "FOREIGN KEY (accountNumber) REFERENCES ACCOUNT(accountNumber))");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
            throw e; // Rethrow the exception to indicate failure
        }
    }

    /**
     * Inserts sample data into the CLIENT, ACCOUNT, and TRANSACTION tables for testing purposes.
     *
     * @param statement The SQL statement to execute.
     * @throws SQLException If an SQL exception occurs during data insertion.
     */
    private static void insertSampleData(Statement statement) throws SQLException {
        try {
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
            statement.executeUpdate("INSERT INTO TRANSACTION (transactionType, amount, accountNumber, newBalance) VALUES ('DEPOSIT', 500, 123, 1500)");
            statement.executeUpdate("INSERT INTO TRANSACTION (transactionType, amount, accountNumber, newBalance) VALUES ('WITHDRAWAL', 200, 123, 1300)");

            // Updating John's balance after the transactions
            statement.executeUpdate("UPDATE ACCOUNT SET balance = 1300 WHERE accountNumber = 123");


        } catch (SQLException e) {
            System.err.println("Error inserting sample data: " + e.getMessage());
            throw e;
        }
    }
}
