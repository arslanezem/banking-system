package org.exercise.model;

public class Message {
    private String messageType;
    private int accountNumber;
    private double amount;

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
