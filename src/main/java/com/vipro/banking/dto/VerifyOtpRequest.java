package com.vipro.banking.dto;

public record VerifyOtpRequest(String email,
                               String otp) {
}
