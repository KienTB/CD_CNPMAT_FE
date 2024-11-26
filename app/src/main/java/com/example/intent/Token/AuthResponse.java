package com.example.intent.Token;

import com.example.intent.Model.User;

public class AuthResponse {
    public String token;
    public User user;

    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public AuthResponse(String token, User user) {
        this.token = token;
        this.user = user;
    }
}
//HANDLE JWT RESPONSE
