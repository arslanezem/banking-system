package org.exercise.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.text.NumberFormatter;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLOutput;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User interface class for banking operations.
 * Allows the user to input account number, amount, and choose an action (Deposit, Withdraw, Balance, History).
 * Sends HTTP requests to the banking server based on user actions and displays the server's response.
 * Used only for testing purposes.
 */
public class User extends JFrame {

    private JFormattedTextField accountNumberField;
    private JFormattedTextField amountField;
    private JTextArea responseArea;

    public User() {
        setTitle("User Interface");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

        DecimalFormat decimalFormat = new DecimalFormat("####");
        decimalFormat.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(decimalFormat);

        formatter.setValueClass(Integer.class);
        formatter.setMinimum(0);
        formatter.setMaximum(Integer.MAX_VALUE);
        formatter.setAllowsInvalid(false);

        accountNumberField = new JFormattedTextField(formatter);
        inputPanel.add(new JLabel("Account Number:"));
        inputPanel.add(accountNumberField);

        amountField = new JFormattedTextField(formatter);
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);

        responseArea = new JTextArea();
        responseArea.setEditable(false);

        JScrollPane responseScrollPane = new JScrollPane(responseArea);
        mainPanel.add(responseScrollPane, BorderLayout.CENTER);

        mainPanel.add(inputPanel, BorderLayout.NORTH);

        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendRequest("DEPOSIT");
            }
        });

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendRequest("WITHDRAW");
            }
        });

        JButton balanceButton = new JButton("Balance");
        balanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendRequest("BALANCE");
            }
        });

        JButton historyButton = new JButton("History");
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendRequest("HISTORY");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 4));
        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(balanceButton);
        buttonPanel.add(historyButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    /**
     * Sends an HTTP request to the banking server based on the specified action.
     * Updates the JTextArea with the server's response.
     *
     * @param action The action to perform (Deposit, Withdraw, Balance, History).
     */
    private void sendRequest(String action) {
        String accountNumber = accountNumberField.getText();
        String nAccountNumber = accountNumber.replaceAll(" ", "");
        String amount = amountField.getText();
        String nAmount = amount.replaceAll(" ", "");

        System.out.println(nAmount);
        System.out.println(nAccountNumber);


        String response = sendHttpRequest(action, nAccountNumber.replace(" ", ""), nAmount.replace(" ", ""));

        // Display the response in the JTextArea
        responseArea.setText(response);
    }

    /**
     * Entry point for the User interface application.
     *
     * @param args Command line arguments (not used).
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new User().setVisible(true);
            }
        });
    }

    /**
     * Sends an HTTP POST request to the banking server with the specified parameters.
     * Converts the server response into a formatted string for display.
     *
     * @param action        The action to perform (Deposit, Withdraw, Balance, History).
     * @param accountNumber The account number.
     * @param amount        The amount for deposit/withdrawal.
     * @return A formatted string representing the server's response.
     */
    private String sendHttpRequest(String action, String accountNumber, String amount) {
        try {
            String urlString = "http://localhost:9090/api/request";
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Json request body
            String jsonInputString = "{\"accountNumber\": \"" + accountNumber + "\", \"amount\": \"" + amount + "\", \"messageType\": \"" + action + "\"}";

            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = parseJson(response.toString());

            Response responseObject = objectMapper.treeToValue(jsonNode, Response.class);

            String responseText = constructStringText(responseObject);

            reader.close();
            return responseText;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Constructs a formatted string based on the server response object.
     *
     * @param r The server response object.
     * @return A formatted string representing the server's response.
     */
    private String constructStringText(Response r) {
        if (r.getStatus().equals("Unknown Account Number."))
            return "There are no accounts associated with the entered account number: "+r.getAccountNumber();
        if (r.getStatus().equals("Amount must be greater than available balance."))
            return "Insufficient balance to withdraw " + r.getAmount() + ", " +
                    "you only have " + r.getBalance() + " in your account.";
        switch (r.getMessageType()) {
            case "DEPOSIT_SUCCESS":
                return "Deposit successful. \nAccount Number: " + r.getAccountNumber() +
                        "\nAmount: " + r.getAmount() +
                        "\nNew Balance: " + r.getBalance();
            case "WITHDRAW_SUCCESS":
                return "Withdrawal successful. \nAccount Number: " + r.getAccountNumber() +
                        "\nAmount: " + r.getAmount() +
                        "\nNew Balance: " + r.getBalance();
            case "BALANCE_SUCCESS":
                return "Balance request successful. \nAccount Number: " + r.getAccountNumber() +
                        "\nBalance: " + r.getBalance();
            case "HISTORY_SUCCESS":

                String transactionHistoryString = r.getTransactionHistory();
                transactionHistoryString = transactionHistoryString.substring(1, transactionHistoryString.length() - 1);
                List<String> transactionList = Arrays.asList(transactionHistoryString.split(", "));

                List<Transaction> transactions = new ArrayList<>();

                for (String transactionString : transactionList) {
                    String[] parts = transactionString.split(": ");
                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    switch (key) {
                        case "Transaction ID":
                            int id = Integer.parseInt(value);
                            transactions.add(new Transaction(id));
                            break;
                        case "Type":
                            transactions.get(transactions.size() - 1).setType(value);
                            break;
                        case "Amount":
                            double amount = Double.parseDouble(value);
                            transactions.get(transactions.size() - 1).setAmount(amount);
                            break;
                        case "Timestamp":
                            LocalDateTime timestamp = LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS"));
                            transactions.get(transactions.size() - 1).setTimestamp(timestamp);
                            break;
                        case "New Balance":
                            int newBalance = Integer.parseInt(value);
                            transactions.get(transactions.size() - 1).setNewBalance(newBalance);
                            break;
                        default:
                            break;
                    }
                }

                StringBuilder transactionDetails = new StringBuilder();

                for (Transaction transaction : transactions) {
                    transactionDetails.append("Type: ").append(transaction.getType()).append("\n");
                    transactionDetails.append("Amount: ").append(transaction.getAmount()).append("\n");
                    transactionDetails.append("Timestamp: ").append(transaction.getTimestamp()).append("\n");
                    transactionDetails.append("New Balance: ").append(transaction.getNewBalance()).append("\n");
                    transactionDetails.append("--------------------------------------------").append("\n");
                }

                return "Transaction history retrieved successfully. \nAccount Number: " + r.getAccountNumber() +
                        "\nTransaction History: " + transactionDetails;
            default:
                return "Unknown message type";
        }
    }

    /**
     * Parses a JSON string into a JsonNode.
     *
     * @param jsonString The JSON string to parse.
     * @return The parsed JsonNode.
     * @throws RuntimeException If an error occurs while parsing JSON.
     */
    public static JsonNode parseJson(String jsonString) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readTree(jsonString);
        } catch (Exception e) {
            throw new RuntimeException("Error parsing JSON", e);
        }
    }

}
