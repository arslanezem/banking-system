package org.exercise.service;

import java.sql.*;
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
            e.printStackTrace();
        }

        return false;
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
            throw new RuntimeException(e);
        }

        // Return 0.0 if the account is not found or an error has occurred
        return 0.0;
    }


    public static List<String> getTransactionHistoryNew(int accountNumber) {
        List<String> transactionHistoryList = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM TRANSACTION WHERE accountNumber = ?")) {

            statement.setInt(1, accountNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int transactionId = resultSet.getInt("id");
                    String transactionType = resultSet.getString("transactionType");
                    double amount = resultSet.getDouble("amount");

                    // Ajoute les détails de la transaction à la liste
                    String transactionDetails = "Transaction ID: " + transactionId +
                            ", Type: " + transactionType +
                            ", Amount: " + amount;
                    transactionHistoryList.add(transactionDetails);
                }
            }
        } catch (SQLException e) {
            // Gestion des erreurs : logger ou remonter l'exception, évitez simplement e.printStackTrace()
            e.printStackTrace();
            // Vous pouvez également retourner une liste vide ou lever une exception en cas d'erreur
        }

        return transactionHistoryList;
    }


    public static String getTransactionHistoryOld(int accountNumber) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM TRANSACTION WHERE accountNumber = ?")) {

            statement.setInt(1, accountNumber);

            try (ResultSet resultSet = statement.executeQuery()) {
                StringBuilder transactionHistory = new StringBuilder();

                while (resultSet.next()) {
                    int transactionId = resultSet.getInt("id");
                    String transactionType = resultSet.getString("transactionType");
                    double amount = resultSet.getDouble("amount");

                    // Ajoute les détails de la transaction à l'historique
                    transactionHistory.append("Transaction ID: ").append(transactionId)
                            .append(", Type: ").append(transactionType)
                            .append(", Amount: ").append(amount)
                            .append("\n");
                }

                return transactionHistory.toString();
            }
        } catch (SQLException e) {
            // Gestion des erreurs : logger ou remonter l'exception, évitez simplement e.printStackTrace()
            e.printStackTrace();
            return "Error retrieving transaction history.";
        }
    }


    public static void processWithdrawalNew(int accountNumber, double amount) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
             PreparedStatement clientStatement = connection.prepareStatement(
                     "SELECT * FROM CLIENT WHERE accountNumber = ?");
             PreparedStatement withdrawalStatement = connection.prepareStatement(
                     "INSERT INTO TRANSACTION (transactionType, amount, accountNumber) VALUES ('WITHDRAWAL', ?, ?)");
             PreparedStatement updateBalanceStatement = connection.prepareStatement(
                     "UPDATE ACCOUNT SET balance = balance - ? WHERE accountNumber = ?")) {

            // Vérifie si le client existe
            clientStatement.setInt(1, accountNumber);
            try (ResultSet clientResultSet = clientStatement.executeQuery()) {
                if (clientResultSet.next()) {
                    // Le client existe, récupère les informations
                    int clientId = clientResultSet.getInt("id");
                    String firstName = clientResultSet.getString("firstName");
                    String lastName = clientResultSet.getString("lastName");
                    int age = clientResultSet.getInt("age");

                    // Insère la transaction associée dans la base de données
                    withdrawalStatement.setDouble(1, amount);
                    withdrawalStatement.setInt(2, accountNumber);
                    withdrawalStatement.executeUpdate();

                    // Met à jour le solde du compte
                    updateBalanceStatement.setDouble(1, amount);
                    updateBalanceStatement.setInt(2, accountNumber);
                    updateBalanceStatement.executeUpdate();

                    // Affiche l'opération dans les logs
                    System.out.println("Withdrawal operation for client: " + firstName + " " + lastName + ", Amount: " + amount);
                } else {
                    // Le client n'existe pas, vous pouvez gérer cette situation en fonction de vos besoins
                    System.out.println("Client with accountNumber " + accountNumber + " does not exist.");
                }
            }

        } catch (SQLException e) {
            // Gestion des erreurs : logger ou remonter l'exception, évitez simplement e.printStackTrace()
            e.printStackTrace();
        }
    }

    public static void processDepositNew(int accountNumber, double amount) {
        try (Connection connection = DriverManager.getConnection("jdbc:h2:mem:testdb", "sa", "password");
             PreparedStatement clientStatement = connection.prepareStatement(
                     "SELECT * FROM CLIENT WHERE accountNumber = ?");
             PreparedStatement depositStatement = connection.prepareStatement(
                     "INSERT INTO TRANSACTION (transactionType, amount, accountNumber) VALUES ('DEPOSIT', ?, ?)");
             PreparedStatement updateBalanceStatement = connection.prepareStatement(
                     "UPDATE ACCOUNT SET balance = balance + ? WHERE accountNumber = ?")) {

            // Vérifie si le client existe
            clientStatement.setInt(1, accountNumber);
            try (ResultSet clientResultSet = clientStatement.executeQuery()) {
                if (clientResultSet.next()) {
                    // Le client existe, récupère les informations
                    int clientId = clientResultSet.getInt("id");
                    String firstName = clientResultSet.getString("firstName");
                    String lastName = clientResultSet.getString("lastName");
                    int age = clientResultSet.getInt("age");

                    // Insère la transaction associée dans la base de données
                    depositStatement.setDouble(1, amount);
                    depositStatement.setInt(2, accountNumber);
                    depositStatement.executeUpdate();

                    // Met à jour la balance dans la table ACCOUNT
                    updateBalanceStatement.setDouble(1, amount);
                    updateBalanceStatement.setInt(2, accountNumber);
                    updateBalanceStatement.executeUpdate();

                    // Affiche l'opération dans les logs
                    System.out.println("Deposit operation for client: " + firstName + " " + lastName + ", Amount: " + amount);
                } else {
                    // Le client n'existe pas, vous pouvez gérer cette situation en fonction de vos besoins
                    System.out.println("Client with accountNumber " + accountNumber + " does not exist.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}