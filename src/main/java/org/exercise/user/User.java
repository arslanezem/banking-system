package org.exercise.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * User interface class for banking operations.
 * Allows the user to input account number, amount, and choose an action (Deposit, Withdraw, Balance, History).
 * Sends HTTP requests to the banking server based on user actions and displays the server's response.
 * Used only for testing purposes.
 */
public class User extends JFrame {

    private JTextField accountNumberField;
    private JTextField amountField;
    private JTextArea responseArea;

    public User() {
        setTitle("User Interface");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

        accountNumberField = new JTextField();
        inputPanel.add(new JLabel("Account Number:"));
        inputPanel.add(accountNumberField);

        amountField = new JTextField();
        inputPanel.add(new JLabel("Amount:"));
        inputPanel.add(amountField);

        responseArea = new JTextArea();
        responseArea.setEditable(false);

        mainPanel.add(inputPanel, BorderLayout.NORTH);
        mainPanel.add(responseArea, BorderLayout.CENTER);

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
        String amount = amountField.getText();

        String response = sendHttpRequest(action, accountNumber, amount);

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
                return "Transaction history retrieved successfully. \nAccount Number: " + r.getAccountNumber() +
                        "\nTransaction History: " + r.getTransactionHistory();
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
