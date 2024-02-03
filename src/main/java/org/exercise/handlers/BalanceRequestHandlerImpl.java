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

        Message responseMessage = new Message();
        int accountNumber = m.getAccountNumber();

        if (ts.accountExists(accountNumber)) {
            double balance = ts.getAccountBalanceByAccountNumber(accountNumber);

            responseMessage.setAccountNumber(accountNumber);
            responseMessage.setMessageType("BALANCE_SUCCESS");
            responseMessage.setBalance(balance);
            responseMessage.setStatus("Balance request successful.");
        }
        else {
            responseMessage.setAccountNumber(accountNumber);
            responseMessage.setMessageType("BALANCE_FAIL");
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

        return response;
    }

}
