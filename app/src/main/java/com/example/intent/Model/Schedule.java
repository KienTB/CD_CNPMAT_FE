package com.example.intent.Model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.time.LocalTime;

public class Schedule {
    @SerializedName("scheduleId")
    private int scheduleId;

    @SerializedName("studentId")
    private Long studentId;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("activity")
    private String activity;

    @SerializedName("scheduleDate")
    private String scheduleDate;

    @SerializedName("status")
    private String status;

    public Schedule(int scheduleId, Long studentId, Long userId, String activity, String scheduleDate, String status) {
        this.scheduleId = scheduleId;
        this.studentId = studentId;
        this.userId = userId;
        this.activity = activity;
        this.scheduleDate = scheduleDate;
        this.status = status;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
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
