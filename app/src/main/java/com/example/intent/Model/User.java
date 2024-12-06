package com.example.intent.Model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class User {
    @SerializedName("userId")
    private Long userId;

    @SerializedName("phoneNumber")
    private String phoneNumber;

    @SerializedName("password")
    private String password;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    @SerializedName("name")
    private String name;

    @SerializedName("address")
    private String address;

    @SerializedName("created_at")
    private LocalDate created_at;

    @SerializedName("updated_at")
    private LocalDate updated_at;

    @SerializedName("teacherId")
    private long teacherId;

    public long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(long teacherId) {
        this.teacherId = teacherId;
    }

    public User(Long user_id, String phoneNumber, String password, String email, String role, String name, String address, LocalDate created_at, LocalDate updated_at, long teacher_id) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.email = email;
        this.role = role;
        this.name = name;
        this.address = address;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.teacherId = teacher_id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int username) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDate created_at) {
        this.created_at = created_at;
    }

    public LocalDate getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(LocalDate updated_at) {
        this.updated_at = updated_at;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
