package org.exercise.user;

import java.time.LocalDateTime;

/**
 * Represents a transaction done by a client.
 */
public class Transaction {
    private int id;
    private LocalDateTime timestamp;
    private int newBalance;

    public Transaction(int id) {
    }

    public int getNewBalance() {
        return newBalance;
    }

    public void setNewBalance(int newBalance) {
        this.newBalance = newBalance;
    }

    public Transaction() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private String type;  // "DEPOSIT", "WITHDRAWAL", etc.
    private double amount;

    public Transaction(int id, String type, LocalDateTime timestamp, double amount, int newBalance) {
        this.id = id;
        this.type = type;
        this.timestamp = timestamp;
        this.amount = amount;
        this.newBalance = newBalance;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction ID: " + id +
                ", Type: " + type +
                ", Amount: " + amount +
                ", Timestamp: " + timestamp +
                ", New Balance: " + newBalance;
    }
}
