package com.vipro.banking.controller;

import com.vipro.banking.dto.AuthResponse;
import com.vipro.banking.dto.LoginRequest;
import com.vipro.banking.dto.RegisterRequest;
import com.vipro.banking.dto.VerifyOtpRequest;
import com.vipro.banking.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
    private AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest registerRequest){
       String response = authService.register(registerRequest);
       return new ResponseEntity(response, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest){
        log.info("Login attempt for user: {}", loginRequest);
        AuthResponse authResponse =  authService.login(loginRequest);
        log.info("Login success for user: {}", authResponse);
      return new ResponseEntity(authResponse,HttpStatus.OK);
    }
    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestBody VerifyOtpRequest request) {
        authService.verifyOtp(request.email(), request.otp());
        return ResponseEntity.ok("Account activated successfully");
    }
    @PostMapping("/resend-otp")
    public ResponseEntity<String> resendOtp(@RequestBody Map<String, String> request) {
        authService.resendOtp(request);
        return ResponseEntity.ok("OTP resent successfully");
    }
    @GetMapping("/is-email-verified")
    public ResponseEntity<Map<String,Boolean>> isEmailVerified(@RequestParam String email){
        boolean verified = authService.isEmailVerified(email);
        return  ResponseEntity.ok(Map.of("verified",verified));
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        // optional: store token in blacklist
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

}
