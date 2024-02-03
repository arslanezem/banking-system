package org.exercise.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.exercise.model.Message;
import org.exercise.handlers.interfaces.MessageHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class RequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        InputStream requestBody = exchange.getRequestBody();

        InputStreamReader isr = new InputStreamReader(requestBody, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);

        StringBuilder requestBodyStringBuilder = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            requestBodyStringBuilder.append(line);
        }

        String requestBodyString = requestBodyStringBuilder.toString();

        ObjectMapper objectMapper = new ObjectMapper();
        Message message = objectMapper.readValue(requestBodyString, Message.class);

        MessageHandler messageHandler = null;
        switch (message.getMessageType()) {
            case "BALANCE":
                messageHandler = new BalanceRequestHandlerImpl();
                break;
            case "WITHDRAW":
                messageHandler = new WithdrawalRequestHandlerImpl();
                break;
            case "HISTORY":
                messageHandler = new HistoryRequestHandlerImpl();
                break;
            case "DEPOSIT":
                messageHandler = new DepositRequestHandlerImpl();
                break;
        }

        int statusCode = 200;
        String response = "Invalid message type";

        // If correct handler, process the request, else "Invalid message type" is returned with 400 status code
        if (messageHandler != null) {
            response = messageHandler.handleRequest(message);

            if (response.contains("FAIL")) {
                statusCode = 400;  // Bad Request
            }
        }
        else {
            statusCode = 400;  // Bad Request
        }

        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
