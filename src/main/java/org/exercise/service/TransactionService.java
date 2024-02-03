package org.exercise.service;

import org.exercise.model.Account;
import org.exercise.model.Client;
import java.sql.*;

import static org.exercise.service.DatabaseInitializer.getConnection;

public class TransactionService {

    private static TransactionService instance;

    // Private constructor to prevent instantiation outside the class
    private TransactionService() {
        // Optional: Initialize any resources or configurations needed by the service
    }

    // Method to get the single instance of TransactionService
    public static synchronized TransactionService getInstance() {
        if (instance == null) {
            instance = new TransactionService();
        }
        return instance;
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

        // Retourne 0.0 si le compte n'est pas trouvé ou s'il y a une erreur
        return 0.0;
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
            // Gestion des erreurs : logger ou remonter l'exception, évitez simplement e.printStackTrace()
            e.printStackTrace();
        }
    }


}
