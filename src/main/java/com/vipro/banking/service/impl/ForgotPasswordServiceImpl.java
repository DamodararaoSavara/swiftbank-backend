package com.vipro.banking.service.impl;

import com.vipro.banking.entity.ForgotPasswordOtp;
import com.vipro.banking.entity.User;
import com.vipro.banking.exception.AccountLockedException;
import com.vipro.banking.exception.AuthenticationException;
import com.vipro.banking.exception.OtpException;
import com.vipro.banking.repository.ForgotPasswordOtpRepository;
import com.vipro.banking.repository.UserRepository;
import com.vipro.banking.service.ForgotPasswordService;
import com.vipro.banking.utility.OtpGenerator;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class ForgotPasswordServiceImpl implements ForgotPasswordService {
    private final UserRepository userRepository;
    private final ForgotPasswordOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpGenerator otpGenerator;
    private final EmailService emailService;

    /* ===================== SEND OTP ===================== */
    public void sendForgotPasswordOtp(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AuthenticationException("Email not registered"));

        otpRepository.deleteByEmail(email);

        ForgotPasswordOtp otp = new ForgotPasswordOtp();
        otp.setEmail(email);
        otp.setOtp(otpGenerator.generateOtp());
        otp.setExpiryTime(LocalDateTime.now().plusSeconds(90));
        otp.setAttempts(0);
        otp.setVerified(false);

        otpRepository.save(otp);

        emailService.sendOtp(email, otp.getOtp());
    }

    /* ===================== VERIFY OTP ===================== */
    public void verifyForgotPasswordOtp(String email, String otpValue) {

        ForgotPasswordOtp otp = otpRepository.findByEmail(email)
                .orElseThrow(() ->
                        new OtpException("OTP not found"));

        if (otp.getExpiryTime().isBefore(LocalDateTime.now())) {
            throw new OtpException("OTP expired");
        }

        if (!otp.getOtp().equals(otpValue)) {
            otp.setAttempts(otp.getAttempts() + 1);
            otpRepository.save(otp);

            if (otp.getAttempts() >= 3) {
                otpRepository.deleteByEmail(email);
                throw new AccountLockedException(
                        "Too many invalid OTP attempts"
                );
            }
            throw new OtpException("Invalid OTP");
        }

        otp.setVerified(true);
        otpRepository.save(otp);
    }

    /* ===================== RESET PASSWORD ===================== */
    public void resetPassword(String email, String newPassword,String confirmPassword) {

        ForgotPasswordOtp otp = otpRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AuthenticationException("OTP verification required"));

        if (!otp.isVerified()) {
            throw new AuthenticationException("OTP not verified");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new AuthenticationException("User not found"));
       if(!newPassword.equals(confirmPassword)){
           throw new AuthenticationException("Passwords do not match");
       }
        user.setPassword(passwordEncoder.encode(confirmPassword));
        //user.setEmailVerified(true);
        userRepository.save(user);
        System.out.println(user.getPassword());
        otpRepository.deleteByEmail(email);
    }
}
