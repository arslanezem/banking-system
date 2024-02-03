package org.exercise.handlers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.exercise.handlers.interfaces.HistoryRequestHandler;
import org.exercise.model.Message;
import org.exercise.model.Transaction;
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
            List<Transaction> transactionHistory = ts.getTransactionHistoryNew(accountNumber);

            for (Transaction transaction : transactionHistory) {
                System.out.println(transaction.getId());
                System.out.println(transaction.getAmount());
                System.out.println(transaction.getTimestamp());
                System.out.println(transaction.getType());
            }

            double balance = ts.getAccountBalanceByAccountNumber(accountNumber);
            if (transactionHistory.isEmpty()) {
                responseMessage.setStatus("No transactions found.");
            }
            responseMessage.setAccountNumber(accountNumber);
            responseMessage.setMessageType("HISTORY_SUCCESS");
            responseMessage.setBalance(balance);
            responseMessage.setStatus(transactionHistory.size() + " transactions found.");
            responseMessage.setTransactionHistory(transactionHistory.toString());
        } else {
            responseMessage.setAccountNumber(accountNumber);
            responseMessage.setMessageType("HISTORY_FAIL");
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
