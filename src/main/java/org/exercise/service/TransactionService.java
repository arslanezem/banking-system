package org.exercise.service;

import org.exercise.model.Account;
import org.exercise.model.Client;
import java.sql.*;

import static org.exercise.service.DatabaseInitializer.getConnection;

public class TransactionService {

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





    public static void processDeposit(int accountNumber, double amount) {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Vérifie si le client existe
            ResultSet clientResultSet = statement.executeQuery(
                    "SELECT * FROM CLIENT WHERE accountNumber = " + accountNumber);

            if (clientResultSet.next()) {
                // Le client existe, récupère les informations
                int clientId = clientResultSet.getInt("id");
                String firstName = clientResultSet.getString("firstName");
                String lastName = clientResultSet.getString("lastName");
                int age = clientResultSet.getInt("age");


                // Crée un objet Client
                Client client = new Client(clientId, firstName, lastName, age);

                // Insère la transaction associée dans la base de données
                statement.executeUpdate(
                        "INSERT INTO TRANSACTION (transactionType, amount, accountNumber) VALUES ('DEPOSIT', " + amount + ", " + accountNumber + ")");

                // Affiche l'opération dans les logs
                System.out.println("Deposit operation for client: " + firstName + " " + lastName + ", Amount: " + amount);
            } else {
                // Le client n'existe pas, vous pouvez gérer cette situation en fonction de vos besoins
                System.out.println("Client with accountNumber " + accountNumber + " does not exist.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }






    public static void deposit(Client client, Account account, double amount) {

        // Vérifie si le compte appartient au client
        if (account.getAccountNumber() == client.getAccountNumber()) {
            // Effectue le dépôt d'argent
            double currentBalance = account.getBalance();
            account.setBalance(currentBalance + amount);

            // Enregistre la transaction dans l'historique du compte (facultatif)
            //account.addTransaction(new Transaction("DEPOSIT", amount));

            // Affiche les journaux
            System.out.println("Transaction successful:");
            System.out.println("Client: " + client.getFirstName() + " " + client.getLastName());
            System.out.println("Amount deposited: " + amount);
            System.out.println("New balance: " + account.getBalance());
        } else {
            System.out.println("Error: The account does not belong to the client.");
        }
    }
}
