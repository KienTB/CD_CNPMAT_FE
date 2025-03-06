package com.example.intent.Request;

public class OtpVerificationRequest {
    private Long userId;
    private String otp;

    public OtpVerificationRequest() {}

    public OtpVerificationRequest(Long userId, String otp) {
        this.userId = userId;
        this.otp = otp;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
