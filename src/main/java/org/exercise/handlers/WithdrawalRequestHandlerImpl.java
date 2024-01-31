package org.exercise.handlers;

import org.exercise.model.Message;
import org.exercise.parsers.JsonParser;
import org.exercise.handlers.interfaces.WithdrawalRequestHandler;

public class WithdrawalRequestHandlerImpl implements WithdrawalRequestHandler {
    @Override
    public String handleRequest(Message m) {
        JsonParser jp = JsonParser.getInstance();

        String response = null;

        try {
            response = jp.convertObjectToJson(m);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }
}
