package com.vipro.banking.service;

import com.vipro.banking.dto.AuthResponse;
import com.vipro.banking.dto.LoginRequest;
import com.vipro.banking.dto.RegisterRequest;

import java.util.Map;

public interface AuthService {
    String register(RegisterRequest registerRequest);
    AuthResponse login(LoginRequest loginRequest);
    void verifyOtp(String email,String otp);
    String resendOtp(Map<String, String> request);
    Boolean isEmailVerified(String email);
}
