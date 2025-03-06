package com.example.intent.Token;

import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    public String token;
    private Long userId;
    private String role;
    private String name;
    private String refreshToken;
    private Integer teacherId;

    public Integer getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Integer teacherId) {
        this.teacherId = teacherId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
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

    public AuthResponse(String token, long userId, String role, String name, String refreshToken, Integer teacherId) {
        this.token = token;
        this.userId = userId;
        this.role = role;
        this.name = name;
        this.refreshToken = refreshToken;
        this.teacherId = teacherId;
    }
}
//HANDLE JWT RESPONSE
