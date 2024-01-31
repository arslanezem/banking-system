package org.exercise.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {
    @JsonProperty("message_type")
    private String messageType;
    @JsonProperty("account_number")
    private int accountNumber;
    @JsonProperty("amount")
    private double amount;

    public Message() {
    }

    public Message(String messageType, int accountNumber, double amount) {
        this.messageType = messageType;
        this.accountNumber = accountNumber;
        this.amount = amount;
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
}
