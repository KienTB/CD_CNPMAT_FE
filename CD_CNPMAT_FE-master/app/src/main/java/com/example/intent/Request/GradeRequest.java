package com.example.intent.Request;

import com.google.gson.annotations.SerializedName;

public class GradeRequest {
    @SerializedName("studentId")
    private Long studentId;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("subject")
    private String subject;

    @SerializedName("score")
    private float score;

    @SerializedName("term")
    private String term;

    public GradeRequest(Long studentId, Long userId, String subject, float score, String term) {
        this.studentId = studentId;
        this.userId = userId;
        this.subject = subject;
        this.score = score;
        this.term = term;
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

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
