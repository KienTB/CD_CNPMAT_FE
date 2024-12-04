package com.example.intent.Model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class Notification {
    @SerializedName("notificationId")
    private Long notificationId;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("recipientRole")
    private String recipientRole;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("created_at")
    private LocalDateTime createdAt;

    @SerializedName("updated_at")
    private LocalDateTime updatedAt;

    public Notification(Long notificationId, String title, String content, String recipientRole, Long userId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.notificationId = notificationId;
        this.title = title;
        this.content = content;
        this.recipientRole = recipientRole;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
