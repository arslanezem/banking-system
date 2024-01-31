package org.exercise.handlers;

import org.exercise.handlers.interfaces.BalanceRequestHandler;
import org.exercise.model.Message;
import org.exercise.parsers.JsonParser;

public class BalanceRequestHandlerImpl implements BalanceRequestHandler {
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
