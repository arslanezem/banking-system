package org.exercise.handlers;

import org.exercise.model.Message;
import org.exercise.parsers.JsonParser;
import org.exercise.handlers.interfaces.DepositRequestHandler;
import org.exercise.service.TransactionService;

public class DepositRequestHandlerImpl implements DepositRequestHandler {
    @Override
    public String handleRequest(Message m) {

        // Créer une instance de TransactionService
        TransactionService ts = TransactionService.getInstance();

        // Créer une instance de JsonParser
        JsonParser jp = JsonParser.getInstance();

        // Récupérer le montant du dépôt et le numéro de compte depuis le message
        double depositAmount = m.getAmount();
        int accountNumber = m.getAccountNumber();

        // Exécuter la logique du dépôt (ajouter le montant au solde du compte)
        ts.processDepositNew(accountNumber, depositAmount);

        double newBalance = ts.getAccountBalanceByAccountNumber(accountNumber);

        Message responseMessage = new Message();
        responseMessage.setAccountNumber(accountNumber);
        responseMessage.setMessageType("DEPOSIT_RESPONSE");
        responseMessage.setBalance(newBalance);
        responseMessage.setAmount(depositAmount);



        // Convertir l'objet Message en JSON
        String response = null;
        try {
            response = jp.convertObjectToJson(responseMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Retourner la réponse JSON
        return response;
    }
}
