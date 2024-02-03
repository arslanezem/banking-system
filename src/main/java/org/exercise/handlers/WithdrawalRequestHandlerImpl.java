package org.exercise.handlers;


import org.exercise.model.Message;
import org.exercise.parsers.JsonParser;
import org.exercise.handlers.interfaces.WithdrawalRequestHandler;
import org.exercise.service.TransactionService;

public class WithdrawalRequestHandlerImpl implements WithdrawalRequestHandler {
    @Override
    public String handleRequest(Message m) {
        JsonParser jp = JsonParser.getInstance();

        TransactionService ts = TransactionService.getInstance();

        int accountNumber = m.getAccountNumber();
        double amount = m.getAmount();

        // Vérifie si le montant est positif
        if (amount <= 0) {
            return "Error: Withdrawal amount must be positive.";
        }

        // Vérifie si le solde est suffisant
        double balance = ts.getAccountBalanceByAccountNumber(accountNumber);

        if (amount > balance) {
            return "Error: Insufficient funds for withdrawal.";
        }

        // Traite le retrait
        ts.processWithdrawalNew(accountNumber, amount);

        // Crée et renvoie la réponse
        double newBalance = ts.getAccountBalanceByAccountNumber(accountNumber);

        Message responseMessage = new Message();
        responseMessage.setAccountNumber(accountNumber);
        responseMessage.setMessageType("WITHDRAW_RESPONSE");
        responseMessage.setBalance(newBalance);
        responseMessage.setAmount(amount);

        // Convertir l'objet Message en JSON
        String response = null;
        try {
            response = jp.convertObjectToJson(responseMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return response;
    }
}
