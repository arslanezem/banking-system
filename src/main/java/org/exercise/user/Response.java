package org.exercise.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {
    private final String transactionHistory;
    private final String status;
    private final String messageType;
    private final int accountNumber;
    private final double amount;
    private final double balance;

    @JsonCreator
    public Response(
            @JsonProperty("transactionHistory") String transactionHistory,
            @JsonProperty("status") String status,
            @JsonProperty("messageType") String messageType,
            @JsonProperty("accountNumber") int accountNumber,
            @JsonProperty("amount") double amount,
            @JsonProperty("balance") double balance) {
        this.transactionHistory = transactionHistory;
        this.status = status;
        this.messageType = messageType;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.balance = balance;
    }

    public String getTransactionHistory() {
        return transactionHistory;
    }

    public String getStatus() {
        return status;
    }

    public String getMessageType() {
        return messageType;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public double getBalance() {
        return balance;
    }
}
