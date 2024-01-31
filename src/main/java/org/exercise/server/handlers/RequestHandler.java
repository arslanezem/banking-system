package org.exercise.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import org.exercise.model.Message;
import org.exercise.parsers.JsonParser;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class RequestHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        JsonParser jp = JsonParser.getInstance();

        String requestMethod = exchange.getRequestMethod();
        String requestUri = exchange.getRequestURI().toString();
        Headers requestHeaders = exchange.getRequestHeaders();
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

        String messageType = message.getMessageType();
        int accountNumber = message.getAccountNumber();
        double amount = message.getAmount();

        Message m = new Message(messageType, accountNumber, amount);

        String response = null;
        try {
            response = jp.convertObjectToJson(m);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
