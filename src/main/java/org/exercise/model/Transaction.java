package org.exercise.model;

import java.time.LocalDateTime;

public class Transaction {
    // Using the timestamp as an id
    private LocalDateTime timestamp;
    private String type;  // "DEPOSIT", "WITHDRAWAL", etc.
    private double amount;

    public Transaction(long id, String type, LocalDateTime timestamp, double amount) {
        this.type = type;
        this.timestamp = timestamp;
        this.amount = amount;
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
}
