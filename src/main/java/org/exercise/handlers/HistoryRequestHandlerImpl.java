package org.exercise.handlers;

import org.exercise.handlers.interfaces.DepositRequestHandler;
import org.exercise.handlers.interfaces.HistoryRequestHandler;
import org.exercise.model.Message;
import org.exercise.parsers.JsonParser;
import org.exercise.service.TransactionService;

import java.util.List;

public class HistoryRequestHandlerImpl implements HistoryRequestHandler {
    @Override
    public String handleRequest(Message m) {
        JsonParser jp = JsonParser.getInstance();
        TransactionService ts = TransactionService.getInstance();

        int accountNumber = m.getAccountNumber();

        // Récupère l'historique des transactions
        List<String> transactionHistory = ts.getTransactionHistoryNew(accountNumber);

        System.out.println(transactionHistory.toString());




        //transactionHistory.add(transactionHistory);

        double balance = ts.getAccountBalanceByAccountNumber(accountNumber);

        // Crée un nouveau message pour la réponse
        Message responseMessage = new Message();
        responseMessage.setMessageType("HISTORY_RESPONSE");
        responseMessage.setAccountNumber(accountNumber);
        responseMessage.setTransactionHistory(transactionHistory);

//        for (String transaction : transactionHistory) {
//            responseMessage.addTransactionToHistory(transaction);
//        }

        responseMessage.setBalance(balance);


        // Convertir l'objet Message en JSON
        String response = null;

//        for (String transaction : transactionHistory) {
//            responseMessage.addTransactionToHistory(transaction);
//        }


        try {
            response = jp.convertObjectToJson(responseMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }
}
