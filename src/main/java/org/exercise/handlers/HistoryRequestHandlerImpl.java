package org.exercise.handlers;

import org.exercise.handlers.interfaces.HistoryRequestHandler;
import org.exercise.model.Client;
import org.exercise.model.Message;
import org.exercise.model.Transaction;
import org.exercise.parsers.JsonParser;
import org.exercise.service.TransactionService;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoryRequestHandlerImpl implements HistoryRequestHandler {
    static List<Transaction> transactionHistory;
    @Override
    public String handleRequest(Message m) {
        TransactionService ts = TransactionService.getInstance();
        JsonParser jp = JsonParser.getInstance();

        Message responseMessage = new Message();
        int accountNumber = m.getAccountNumber();

        if (ts.accountExists(accountNumber)) {
            transactionHistory = ts.getTransactionHistory(accountNumber);

            double balance = ts.getAccountBalanceByAccountNumber(accountNumber);

            responseMessage.setAccountNumber(accountNumber);
            responseMessage.setMessageType("HISTORY_SUCCESS");
            responseMessage.setBalance(balance);
            responseMessage.setStatus(transactionHistory.size() + " transactions found.");
            responseMessage.setTransactionHistory(transactionHistory.toString());
        } else {
            responseMessage.setAccountNumber(accountNumber);
            responseMessage.setMessageType("HISTORY_FAIL");
            responseMessage.setBalance(0);
            responseMessage.setStatus("Unknown Account Number.");
        }

        // Convert Object Message to JSON
        String response = null;
        try {
            response = jp.convertObjectToJson(responseMessage);
        } catch (Exception e) {
            System.err.println("Error during JSON conversion: " + e.getMessage());
            throw new RuntimeException("Error during JSON conversion", e);
        }

        printResponse(responseMessage);

        return response;
    }

    @Override
    public void printResponse(Message m) {
        System.out.println("Transaction History:");

        if(m.getStatus().equals("Unknown Account Number.")) {
            System.out.println("-------------------------------------------");
            System.out.println("There are no accounts associated with the number account you entered: " + m.getAccountNumber() + ".");
            System.out.println("-------------------------------------------");
            System.out.println("");
        }
        else {
            TransactionService ts = TransactionService.getInstance();
            Client client = ts.getClientByAccountNumber(m.getAccountNumber());

            System.out.println("-------------------------------------------");
            System.out.println("Client Details:");
            System.out.println("Name: " + client.getFirstName() + " " + client.getLastName());
            System.out.println("Account Number: " + client.getAccountNumber());
            System.out.println("Age: " + client.getAge());
            System.out.println("-------------------------------------------");

            if (m.getStatus().equals("0 transactions found.")) {
                System.out.println("There are no transactions made in this account.");
                System.out.println("-------------------------------------------");
                System.out.println("");
            } else {
                System.out.println(transactionHistory.size() +" transactions to display.");
                System.out.println("-------------------------------------------");
                for (Transaction transaction : transactionHistory) {
                    System.out.println("Time: " + transaction.getTimestamp().format(DateTimeFormatter.ofPattern("dd/MM/yyyy, 'at' HH:mm")));
                    System.out.println("Type: " + transaction.getType());
                    System.out.println("Amount: " + transaction.getAmount() + " Euros");
                    System.out.println("--------------------------------------------");
                }
                System.out.println("");
            }
        }

    }
}
