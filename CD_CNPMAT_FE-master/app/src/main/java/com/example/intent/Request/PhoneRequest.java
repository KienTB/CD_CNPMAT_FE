package com.example.intent.Request;

public class PhoneRequest {
    private String phoneNumber;

    public PhoneRequest() {}

    public PhoneRequest(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
