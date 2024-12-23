package com.example.intent.Api;

public class ScheduleApiResponse <Schedule> {
    private boolean success;
    private String message;
    private Schedule data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Schedule getData() {
        return data;
    }

    public ScheduleApiResponse(boolean success, String message, Schedule data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
