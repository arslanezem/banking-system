package org.exercise.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.exercise.model.Message;
import org.exercise.handlers.interfaces.MessageHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Implementation of the {@link HttpHandler} interface for handling HTTP requests.
 * This class reads the incoming message, determines the appropriate message handler, and sends back the response.
 */
public class RequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {

        // Read the incoming message from the request body
        InputStream requestBody = exchange.getRequestBody();
        InputStreamReader isr = new InputStreamReader(requestBody, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        StringBuilder requestBodyStringBuilder = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            requestBodyStringBuilder.append(line);
        }
        String requestBodyString = requestBodyStringBuilder.toString();

        // Deserialize the JSON message into a Message object
        ObjectMapper objectMapper = new ObjectMapper();
        Message message = objectMapper.readValue(requestBodyString, Message.class);

        // Determine the appropriate message handler based on the message type
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
        String response = "Invalid message type.";

        // If correct handler, process the request, else "Invalid message type" is returned
        if (messageHandler != null) {
            response = messageHandler.handleRequest(message);
        }
        else {
            System.out.println("");
            System.out.println(response);
            System.out.println("");
        }

        // Send the HTTP response
        exchange.sendResponseHeaders(statusCode, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
