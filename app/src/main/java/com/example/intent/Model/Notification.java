package com.example.intent.Model;

import com.google.gson.annotations.SerializedName;


public class Notification {
    @SerializedName("notificationId")
    private Long notificationId;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("userId")
    private Long userId;

    public Notification(Long notificationId, String title, String content, Long userId) {
        this.notificationId = notificationId;
        this.title = title;
        this.content = content;
        this.userId = userId;
    }

    public Long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Long notificationId) {
        this.notificationId = notificationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
