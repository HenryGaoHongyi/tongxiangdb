package com.tongxiangdb.dto;

public class ClientOverdueDTO {
    private String name;
    private String phone;
    private String email;
    private Double totalAccumulatedBalance;

    // Constructor
    public ClientOverdueDTO(String name, String phone, String email, Double totalAccumulatedBalance) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.totalAccumulatedBalance = totalAccumulatedBalance;
    }

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getTotalAccumulatedBalance() {
        return totalAccumulatedBalance;
    }

    public void setTotalAccumulatedBalance(Double totalAccumulatedBalance) {
        this.totalAccumulatedBalance = totalAccumulatedBalance;
    }
}
