package org.exercise.handlers;

import org.exercise.handlers.interfaces.HistoryRequestHandler;
import org.exercise.model.Message;
import org.exercise.parsers.JsonParser;
import org.exercise.service.TransactionService;

import java.util.List;

public class HistoryRequestHandlerImpl implements HistoryRequestHandler {
    @Override
    public String handleRequest(Message m) {
        TransactionService ts = TransactionService.getInstance();
        JsonParser jp = JsonParser.getInstance();

        Message responseMessage = new Message();
        int accountNumber = m.getAccountNumber();

        if (ts.accountExists(accountNumber)) {
            List<String> transactionHistory = ts.getTransactionHistoryNew(accountNumber);
            double balance = ts.getAccountBalanceByAccountNumber(accountNumber);
            if (transactionHistory.isEmpty()) {
                responseMessage.setStatus("No transactions found");
            }
            responseMessage.setAccountNumber(accountNumber);
            responseMessage.setMessageType("HISTORY_SUCCESS");
            responseMessage.setBalance(balance);
            responseMessage.setTransactionHistory(transactionHistory);
        } else {
            responseMessage.setAccountNumber(accountNumber);
            responseMessage.setMessageType("HISTORY_FAIL");
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
