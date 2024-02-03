package org.exercise.handlers;

import org.exercise.handlers.interfaces.BalanceRequestHandler;
import org.exercise.model.Message;
import org.exercise.parsers.JsonParser;
import org.exercise.service.TransactionService;

public class BalanceRequestHandlerImpl implements BalanceRequestHandler {
    @Override
    public String handleRequest(Message m) {

        TransactionService ts = TransactionService.getInstance();

        JsonParser jp = JsonParser.getInstance();

        int accountNumber = m.getAccountNumber();

        double balance = ts.getAccountBalanceByAccountNumber(accountNumber);

        Message responseMessage = new Message();
        responseMessage.setAccountNumber(accountNumber);
        responseMessage.setMessageType("BALANCE RESPONSE");
        responseMessage.setBalance(balance);

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
