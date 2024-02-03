package org.exercise.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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
    @JsonIgnore
    @JsonProperty("transactionHistory")
    private List<String> transactionHistory;

    @JsonIgnore
    @JsonProperty("status")
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

    public List<String> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(List<String> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }

    // Add a transaction to history
    public void addTransactionToHistory(String transaction) {
        this.transactionHistory.add(transaction);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
