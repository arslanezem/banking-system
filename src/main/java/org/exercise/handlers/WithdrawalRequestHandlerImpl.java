package org.exercise.handlers;


import org.exercise.model.Message;
import org.exercise.parsers.JsonParser;
import org.exercise.handlers.interfaces.WithdrawalRequestHandler;
import org.exercise.service.TransactionService;

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
                    ts.processWithdrawalNew(accountNumber, amount);
                    double newBalance = ts.getAccountBalanceByAccountNumber(accountNumber);
                    responseMessage.setAccountNumber(accountNumber);
                    responseMessage.setMessageType("WITHDRAW_SUCCESS");
                    responseMessage.setBalance(newBalance);
                    responseMessage.setAmount(amount);
                } else {
                    responseMessage.setAccountNumber(accountNumber);
                    responseMessage.setMessageType("WITHDRAW_FAIL");
                    responseMessage.setStatus("Amount must be greater than available balance");
                    responseMessage.setAmount(amount);
                    responseMessage.setAmount(balance);
                }
            } else{
                responseMessage.setAccountNumber(accountNumber);
                responseMessage.setMessageType("WITHDRAW_FAIL");
                responseMessage.setStatus("Amount must be greater than 0");
                responseMessage.setAmount(amount);
            }
        } else {
            responseMessage.setAccountNumber(accountNumber);
            responseMessage.setMessageType("WITHDRAW_FAIL");
            responseMessage.setBalance(0);
            responseMessage.setStatus("Unknown Account Number");
        }


        // Convert Object Message to JSON
        String response = null;
        try {
            response = jp.convertObjectToJson(responseMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }
}
