package org.exercise.handlers;

import org.exercise.model.Client;
import org.exercise.model.Message;
import org.exercise.parsers.JsonParser;
import org.exercise.handlers.interfaces.DepositRequestHandler;
import org.exercise.service.TransactionService;

/**
 * Implementation of the {@link DepositRequestHandler} interface for handling deposit requests.
 * This class provides the logic for processing deposit requests and generating appropriate responses.
 */
public class DepositRequestHandlerImpl implements DepositRequestHandler {

    /**
     * Handles a deposit request and generates an appropriate response.
     *
     * @param m The {@link Message} representing the deposit request.
     * @return The JSON-formatted response message.
     * @throws RuntimeException If there is an error during JSON conversion.
     */
    @Override
    public String handleRequest(Message m) {
        TransactionService ts = TransactionService.getInstance();
        JsonParser jp = JsonParser.getInstance();

        Message responseMessage = new Message();
        int accountNumber = m.getAccountNumber();
        double depositAmount = m.getAmount();

        // Check if the account exists
        if (ts.accountExists(accountNumber)) {
            // Process the deposit (database)
            ts.processDeposit(accountNumber, depositAmount);

            // Get the new balance
            double newBalance = ts.getAccountBalanceByAccountNumber(accountNumber);

            // Set response details
            responseMessage.setAccountNumber(accountNumber);
            responseMessage.setMessageType("DEPOSIT_SUCCESS");
            responseMessage.setBalance(newBalance);
            responseMessage.setAmount(depositAmount);
            responseMessage.setStatus("Deposit Request successful.");

        } else {
            // Set response details for unknown account number
            responseMessage.setAccountNumber(accountNumber);
            responseMessage.setMessageType("DEPOSIT_FAIL");
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

        // Print the response details
        printResponse(responseMessage);

        return response;
    }

    /**
     * Prints the details of the deposit response to the console.
     *
     * @param m The {@link Message} representing the deposit response.
     */
    @Override
    public void printResponse(Message m) {
        System.out.println("Deposit Request:");

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

            System.out.println("Your deposit request of " + m.getAmount() + " Euros has been successful.");
            System.out.println("Your new balance is: " + m.getBalance() + " Euros.");
            System.out.println("-------------------------------------------");
            System.out.println("");
        }
    }
}
