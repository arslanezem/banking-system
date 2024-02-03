package org.exercise.service;

import org.exercise.model.Client;
import org.exercise.model.Transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


public class TransactionService {

    private static TransactionService instance;

    // Private constructor to prevent instantiation outside the class
    private TransactionService() {
    }

    // Method to get the single instance of TransactionService
    public static synchronized TransactionService getInstance() {
        if (instance == null) {
            instance = new TransactionService();
        }
        return instance;
    }

    public static boolean accountExists(int accountNumber) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM CLIENT WHERE accountNumber = ?")) {
            statement.setInt(1, accountNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error checking account existence", e);

        }

        return false;
    }

    public Client getClientByAccountNumber(int accountNumber) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM CLIENT WHERE accountNumber = ?")) {

            statement.setInt(1, accountNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int clientId = resultSet.getInt("id");
                    String firstName = resultSet.getString("firstName");
                    String lastName = resultSet.getString("lastName");
                    int age = Integer.parseInt(resultSet.getString("age"));

                    return new Client(accountNumber, firstName, lastName, age);
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error getting client by account number", e);
        }

        return null;
    }

    public static double getAccountBalanceByAccountNumber(int accountNumber) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password")) {
            String query = "SELECT balance FROM Account WHERE accountNumber = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, accountNumber);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getDouble("balance");
                    }
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error getting account balance", e);
        }

        // Return 0.0 if the account is not found or an error has occurred
        return 0.0;
    }

    public static List<Transaction> getTransactionHistory(int accountNumber) {
        List<Transaction> transactionHistory = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM TRANSACTION WHERE accountNumber = ?")) {

            statement.setInt(1, accountNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int transactionId = resultSet.getInt("id");
                    String transactionType = resultSet.getString("transactionType");
                    double amount = resultSet.getDouble("amount");
                    Timestamp timestamp = resultSet.getTimestamp("timestamp");

                    LocalDateTime localDateTime = timestamp.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

                    Transaction transaction = new Transaction();
                    transaction.setId(transactionId);
                    transaction.setType(transactionType);
                    transaction.setAmount(amount);
                    transaction.setTimestamp(localDateTime);

                    transactionHistory.add(transaction);

         }
            }
        } catch (SQLException e) {
            handleSQLException("Error getting transaction history", e);
        }

        return transactionHistory;
    }

    public static void processWithdrawalNew(int accountNumber, double amount) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
             PreparedStatement clientStatement = connection.prepareStatement(
                     "SELECT * FROM CLIENT WHERE accountNumber = ?");
             PreparedStatement withdrawalStatement = connection.prepareStatement(
                     "INSERT INTO TRANSACTION (transactionType, amount, accountNumber) VALUES ('WITHDRAWAL', ?, ?)");
             PreparedStatement updateBalanceStatement = connection.prepareStatement(
                     "UPDATE ACCOUNT SET balance = balance - ? WHERE accountNumber = ?")) {

            // Check if client exists (already checked but just in case)
            clientStatement.setInt(1, accountNumber);
            try (ResultSet clientResultSet = clientStatement.executeQuery()) {
                if (clientResultSet.next()) {

                    // Insert the Transaction
                    withdrawalStatement.setDouble(1, amount);
                    withdrawalStatement.setInt(2, accountNumber);
                    withdrawalStatement.executeUpdate();

                    // Update Account Balance
                    updateBalanceStatement.setDouble(1, amount);
                    updateBalanceStatement.setInt(2, accountNumber);
                    updateBalanceStatement.executeUpdate();

                } else {
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error processing withdrawal", e);
        }
    }

    public static void processDeposit(int accountNumber, double amount) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
             PreparedStatement clientStatement = connection.prepareStatement(
                     "SELECT * FROM CLIENT WHERE accountNumber = ?");
             PreparedStatement depositStatement = connection.prepareStatement(
                     "INSERT INTO TRANSACTION (transactionType, amount, accountNumber) VALUES ('DEPOSIT', ?, ?)");
             PreparedStatement updateBalanceStatement = connection.prepareStatement(
                     "UPDATE ACCOUNT SET balance = balance + ? WHERE accountNumber = ?")) {

            // Check if client exists (already checked but just in case)
            clientStatement.setInt(1, accountNumber);
            try (ResultSet clientResultSet = clientStatement.executeQuery()) {
                if (clientResultSet.next()) {

                    // Insert the Transaction in the database
                    depositStatement.setDouble(1, amount);
                    depositStatement.setInt(2, accountNumber);
                    depositStatement.executeUpdate();

                    // Update the balance of the Account
                    updateBalanceStatement.setDouble(1, amount);
                    updateBalanceStatement.setInt(2, accountNumber);
                    updateBalanceStatement.executeUpdate();
                } else {
                }
            }
        } catch (SQLException e) {
            handleSQLException("Error processing deposit", e);
        }
    }

    private static void handleSQLException(String message, SQLException e) {
        System.err.println(message + ": " + e.getMessage());
        throw new RuntimeException(message, e);
    }
}
