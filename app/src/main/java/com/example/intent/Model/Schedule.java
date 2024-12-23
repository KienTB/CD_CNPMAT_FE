package com.example.intent.Model;

import com.google.gson.annotations.SerializedName;


public class Schedule {
    @SerializedName("scheduleId")
    private Long scheduleId;

    @SerializedName("student")
    private Student student;

    @SerializedName("user")
    private User user;

    @SerializedName("activity")
    private String activity;

    @SerializedName("scheduleDate")
    private String scheduleDate;

    @SerializedName("status")
    private String status;

    public Schedule(Long scheduleId, Student student, User user, String activity, String scheduleDate, String status) {
        this.scheduleId = scheduleId;
        this.student = student;
        this.user = user;
        this.activity = activity;
        this.scheduleDate = scheduleDate;
        this.status = status;
    }

    public Long getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
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

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
