package com.example.intent.Request;

public class ResetPasswordRequest {
    private Long userId;
    private String otp;
    private String newPassword;
    public ResetPasswordRequest(Long userId, String otp, String newPassword) {
        this.userId = userId;
        this.otp = otp;
        this.newPassword = newPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
