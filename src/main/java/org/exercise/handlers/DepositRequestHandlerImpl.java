package org.exercise.handlers;

import org.exercise.model.Client;
import org.exercise.model.Message;
import org.exercise.parsers.JsonParser;
import org.exercise.handlers.interfaces.DepositRequestHandler;
import org.exercise.service.TransactionService;

public class DepositRequestHandlerImpl implements DepositRequestHandler {
    @Override
    public String handleRequest(Message m) {
        TransactionService ts = TransactionService.getInstance();
        JsonParser jp = JsonParser.getInstance();

        Message responseMessage = new Message();
        int accountNumber = m.getAccountNumber();
        double depositAmount = m.getAmount();

        if (ts.accountExists(accountNumber)) {
            ts.processDepositNew(accountNumber, depositAmount);

            double newBalance = ts.getAccountBalanceByAccountNumber(accountNumber);

            responseMessage.setAccountNumber(accountNumber);
            responseMessage.setMessageType("DEPOSIT_SUCCESS");
            responseMessage.setBalance(newBalance);
            responseMessage.setAmount(depositAmount);
            responseMessage.setStatus("Deposit Request successful.");

        } else {
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
            throw new RuntimeException(e);
        }

        printResponse(responseMessage);

        return response;
    }

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
