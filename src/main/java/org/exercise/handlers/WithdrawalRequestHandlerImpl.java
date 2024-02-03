package org.exercise.handlers;


import org.exercise.model.Client;
import org.exercise.model.Message;
import org.exercise.parsers.JsonParser;
import org.exercise.handlers.interfaces.WithdrawalRequestHandler;
import org.exercise.service.TransactionService;

/**
 * Implementation of the {@link WithdrawalRequestHandler} interface for handling withdrawal requests.
 * This class provides the logic for processing withdrawal requests and generating appropriate responses.
 */
public class WithdrawalRequestHandlerImpl implements WithdrawalRequestHandler {
    @Override
    public String handleRequest(Message m) {
        TransactionService ts = TransactionService.getInstance();
        JsonParser jp = JsonParser.getInstance();

        int accountNumber = m.getAccountNumber();
        double amount = m.getAmount();
        Message responseMessage = new Message();

        if (ts.accountExists(accountNumber)) {
            double balance = ts.getAccountBalanceByAccountNumber(accountNumber);
            if (amount > 0) {
                if (balance >= amount) {
                    ts.processWithdrawal(accountNumber, amount);
                    double newBalance = ts.getAccountBalanceByAccountNumber(accountNumber);
                    responseMessage.setAccountNumber(accountNumber);
                    responseMessage.setMessageType("WITHDRAW_SUCCESS");
                    responseMessage.setStatus(amount + " euros has been withdrawn. Your new balance is: " + newBalance);
                    responseMessage.setBalance(newBalance);
                    responseMessage.setAmount(amount);
                } else {
                    responseMessage.setAccountNumber(accountNumber);
                    responseMessage.setMessageType("WITHDRAW_FAIL");
                    responseMessage.setStatus("Amount must be greater than available balance.");
                    responseMessage.setAmount(amount);
                    responseMessage.setBalance(balance);
                }
            } else{
                responseMessage.setAccountNumber(accountNumber);
                responseMessage.setMessageType("WITHDRAW_FAIL");
                responseMessage.setStatus("Amount to withdraw must be greater than 0.");
                responseMessage.setAmount(amount);
            }
        } else {
            responseMessage.setAccountNumber(accountNumber);
            responseMessage.setMessageType("WITHDRAW_FAIL");
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

    /**
     * Prints the details of the withdrawal response to the console.
     *
     * @param m The {@link Message} representing the deposit response.
     */
    @Override
    public void printResponse(Message m) {
        System.out.println("Withdrawal Request:");

        if(m.getStatus().equals("Unknown Account Number.")) {
            System.out.println("-------------------------------------------");
            System.out.println("There are no associated accounts with the number account you entered: " + m.getAccountNumber() + ".");
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

            if (m.getStatus().equals("Amount to withdraw must be greater than 0.")) {
                System.out.println("The amount to withdraw must be greater than 0: " + m.getAmount());
                System.out.println("-------------------------------------------");
                System.out.println("");
            } else if (m.getStatus().equals("Amount must be greater than available balance.")) {
                System.out.println("Insufficient balance to withdraw " + m.getAmount() + ", " +
                        "you only have " + m.getBalance() + " in your account.");
                System.out.println("-------------------------------------------");
                System.out.println("");
            } else {
                System.out.println("Your withdrawal request of " + m.getAmount() + " Euros has been successful.");
                System.out.println("Your new balance is: " + m.getBalance() + " Euros.");
                System.out.println("-------------------------------------------");
                System.out.println("");
            }
        }
    }
}
