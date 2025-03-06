package com.example.intent.Request;

import com.google.gson.annotations.SerializedName;

public class LoginRequest {
    @SerializedName("phone_number")
    private String phoneNumber;

    @SerializedName("password")
    private String password;

    public LoginRequest(String phoneNumber, String password) {
        this.phoneNumber = phoneNumber;
        this.password = password;
    }
}
