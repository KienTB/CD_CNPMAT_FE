package com.example.intent.Request;

public class ResetPasswordRequest {
    private String phoneNumber;
    private String otpCode;
    private String newPassword;

    public ResetPasswordRequest(String phoneNumber, String otpCode, String newPassword) {
        this.phoneNumber = phoneNumber;
        this.otpCode = otpCode;
        this.newPassword = newPassword;
    }

}
