package com.example.intent.Model;

import com.google.gson.annotations.SerializedName;

public class Student {
    @SerializedName("studentId")
    private Long studentId;

    @SerializedName("name")
    private String name;

    @SerializedName("birthDate")
    private String birthDate;

    @SerializedName("gender")
    private String gender;

    @SerializedName("class_name")
    private String class_name;

    @SerializedName("address")
    private String address;

    @SerializedName("userId")
    private User user;

    @SerializedName("teacherId")
    private User teacher;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Student(Long studentId, String name, String birthDate, String gender, String class_name, String address, User user, User teacher) {
        this.studentId = studentId;
        this.name = name;
        this.birthDate = birthDate;
        this.gender = gender;
        this.class_name = class_name;
        this.address = address;
        this.user = user;
        this.teacher = teacher;
    }
}
