package com.example.intent.Model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class Grade {
    @SerializedName("gradeId")
    private Long gradeId;

    @SerializedName("student")
    private Student student;

    @SerializedName("user")
    private User user;

    @SerializedName("subject")
    private String subject;

    @SerializedName("score")
    private Float score;

    @SerializedName("term")
    private String term;

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Grade(Long gradeId, Float score, Student student, String subject, String term, User user) {
        this.gradeId = gradeId;
        this.score = score;
        this.student = student;
        this.subject = subject;
        this.term = term;
        this.user = user;
    }
}
