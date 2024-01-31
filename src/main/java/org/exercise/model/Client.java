package org.exercise.model;

public class Client {
    private int accountNumber;
    private String firstName;
    private String lastName;
    private int age;

//    private String address;
//    private String phoneNumber;
//    private String email;
//    private LocalDate dateOfBirth;
//    private MaritalStatus maritalStatus; // Enumération

    public Client(int accountNumber, String firstName, String lastName, int age) {
        this.accountNumber = accountNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    public int getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(int accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
