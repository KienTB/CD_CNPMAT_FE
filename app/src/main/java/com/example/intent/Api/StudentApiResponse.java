package com.example.intent.Api;

public class StudentApiResponse <DataStudent>{
    private boolean success;
    private String message;
    private DataStudent data;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public DataStudent getData() {
        return data;
    }

    public StudentApiResponse(boolean success, String message, DataStudent data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
