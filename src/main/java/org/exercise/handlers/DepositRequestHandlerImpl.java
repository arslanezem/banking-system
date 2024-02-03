package org.exercise.handlers;

import org.exercise.model.Message;
import org.exercise.parsers.JsonParser;
import org.exercise.handlers.interfaces.DepositRequestHandler;

public class DepositRequestHandlerImpl implements DepositRequestHandler {
    @Override
    public String handleRequest(Message m) {

        JsonParser jp = JsonParser.getInstance();

        double amount = m.getAmount();
        int accountNumber = m.getAccountNumber();

        String response = null;

        try {
            response = jp.convertObjectToJson(m);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
