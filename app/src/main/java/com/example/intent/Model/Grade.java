package com.example.intent.Model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class Grade {
    @SerializedName("gradeId")
    private Long gradeId;

    @SerializedName("studentId")
    private Long studentId;

    @SerializedName("userId")
    private Long userId;

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

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public Grade(Long gradeId, Long studentId, Long userId, String subject, Float score, String term, LocalDate createdAt, LocalDate updatedAt) {
        this.gradeId = gradeId;
        this.studentId = studentId;
        this.userId = userId;
        this.subject = subject;
        this.score = score;
        this.term = term;
    }

}
