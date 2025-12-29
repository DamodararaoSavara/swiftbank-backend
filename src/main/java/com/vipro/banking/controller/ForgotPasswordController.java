package com.vipro.banking.controller;

import com.vipro.banking.service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class ForgotPasswordController {

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody Map<String, String> req) {

        forgotPasswordService.sendForgotPasswordOtp(
                req.get("email")
        );
        return ResponseEntity.ok("OTP sent to email");
    }

    @PostMapping("/verify-forgot-otp")
    public ResponseEntity<?> verifyOtp(
            @RequestBody Map<String, String> req) {

        forgotPasswordService.verifyForgotPasswordOtp(
                req.get("email"),
                req.get("otp")
        );
        return ResponseEntity.ok("OTP verified");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody Map<String, String> req) {

        forgotPasswordService.resetPassword(
                req.get("email"),
                req.get("newPassword"),
                req.get("confirmPassword")
        );
        return ResponseEntity.ok("Password reset successful");
    }
}
