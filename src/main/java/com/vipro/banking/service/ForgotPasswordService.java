package com.vipro.banking.service;

public interface ForgotPasswordService {
    public void sendForgotPasswordOtp(String email);
    public void verifyForgotPasswordOtp(String email, String otpValue);
    public void resetPassword(String email, String newPassword,String confirmPassword);
}
