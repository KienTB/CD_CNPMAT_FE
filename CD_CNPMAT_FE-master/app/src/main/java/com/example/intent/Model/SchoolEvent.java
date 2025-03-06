package com.example.intent.Model;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;

public class SchoolEvent {
    @SerializedName("eventId")
    private Long eventId;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("eventDate")
    private LocalDate eventDate;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("created_at")
    private LocalDate createdAt;

    @SerializedName("updated_at")
    private LocalDate updatedAt;

    public SchoolEvent(Long eventId, String title, String description, LocalDate eventDate, Long userId, LocalDate createdAt, LocalDate updatedAt) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters...
}
