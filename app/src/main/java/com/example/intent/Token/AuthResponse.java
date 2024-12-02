package com.example.intent.Token;

import com.example.intent.Model.User;

public class AuthResponse {
    public String token;
    private int userId;
    private String role;
    private String name;
    private String refreshToken;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public AuthResponse(String token, int userId, String role, String name, String refreshToken) {
        this.token = token;
        this.userId = userId;
        this.role = role;
        this.name = name;
        this.refreshToken = refreshToken;
    }
}
//HANDLE JWT RESPONSE
