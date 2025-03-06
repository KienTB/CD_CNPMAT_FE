package com.example.intent.Request;

import com.google.gson.annotations.SerializedName;

public class StudentRegisterRequest {
    @SerializedName("name")
    private String name;
    @SerializedName("birthDate")
    private String birthDate;
    @SerializedName("gender")
    private String gender;
    @SerializedName("class_name")
    private String class_name;
    @SerializedName("userId")
    private Long userId;
    @SerializedName("address")
    private String address;
    @SerializedName("teacherId")
    private Long teacherId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public StudentRegisterRequest(String name, String birthDate, String gender, String class_name, Long userId, String address, Long teacherId) {
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.class_name = class_name;
        this.userId = userId;
        this.address = address;
        this.teacherId = teacherId;
    }
}
