package org.exercise.model;

import java.util.ArrayList;
import java.util.List;

public class Account {
    private int accountNumber;
    private double balance;
    private List<Transaction> transactionHistory;

    public Account(int accountNumber) {
        this.accountNumber = accountNumber;
        this.balance = 0.0;
        this.transactionHistory = new ArrayList<>();
    }

    public void addTransaction(Transaction transaction) {
        transactionHistory.add(transaction);
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<Transaction> getTransactionHistory() {
        return transactionHistory;
    }

    public void setTransactionHistory(List<Transaction> transactionHistory) {
        this.transactionHistory = transactionHistory;
    }
}
