package org.exercise.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Represents the message object that the application receive in the HTTP request.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    @JsonProperty("messageType")
    private String messageType;
    @JsonProperty("accountNumber")
    private int accountNumber;
    @JsonProperty("amount")
    private double amount;
    @JsonProperty("balance")
    private double balance;
    @JsonProperty("transactionHistory")
    @JsonIgnore
    private String transactionHistory;
    @JsonProperty("status")
    @JsonIgnore
    private String status;


    public Message() {
    }

    public Message(String messageType, int accountNumber, double amount, double balance) {
        this.messageType = messageType;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.balance = balance;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(String transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
