package com.vipro.banking.service.impl;

import com.vipro.banking.dto.AuthResponse;
import com.vipro.banking.dto.LoginRequest;
import com.vipro.banking.dto.RegisterRequest;
import com.vipro.banking.entity.Account;
import com.vipro.banking.entity.Role;
import com.vipro.banking.entity.User;
import com.vipro.banking.exception.AccountLockedException;
import com.vipro.banking.exception.AuthenticationException;
import com.vipro.banking.exception.OtpException;
import com.vipro.banking.exception.RegisterApiException;
import com.vipro.banking.jwt.JwtTokenProvider;
import com.vipro.banking.repository.AccountRepository;
import com.vipro.banking.repository.RoleRepository;
import com.vipro.banking.repository.UserRepository;
import com.vipro.banking.service.AuthService;
import com.vipro.banking.utility.OtpGenerator;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private JwtTokenProvider jwtTokenProvider;
    private AuthenticationManager authenticationManager;
    private OtpGenerator otpGenerator;
    private EmailService emailService;
    private AccountRepository accountRepository;
    //private SmsService smsService;
    @Override
    public String register(RegisterRequest registerRequest) {
        //verify the weather the user is existed or not using email filed
       if(userRepository.existsByEmail(registerRequest.email())){
           throw new RegisterApiException(HttpStatus.BAD_REQUEST,"Email already exist");
       }
        //verify the weather the user is existed or not using username filed
       if(userRepository.existsByUsername(registerRequest.username())){
           throw new RegisterApiException(HttpStatus.BAD_REQUEST,"Username already exist");
       }
        User user = new User();
        //user.setName(registerRequest.name());
        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setPassword(passwordEncoder.encode(registerRequest.password()));

        Set<Role> roles = new HashSet<>();
        Role role = roleRepository.findByName("ROLE_USER");
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
        return "User Registered successfully";
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByUsernameOrEmail(loginRequest.usernameOrEmail(), loginRequest.usernameOrEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User does not exist by Username or email"));
        if (!user.isEmailVerified()) {
            throw new AuthenticationException("Email not verified. Please verify OTP.");
        }
        if(user.getAccountStatus().equals("BLOCKED")){
            throw new AuthenticationException("Account blocked by bank.");
        }
        if(user.getAccountStatus().equals("INACTIVE")){
            throw new AuthenticationException("Account not activated. Verify OTP.");
        }
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.usernameOrEmail(),
                loginRequest.password()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .toList();

        System.out.println(token);
        return new AuthResponse(token, roles, user.getId());
    }
    public void verifyOtp(String email, String otp) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Email not found"));

        if (user.isEmailVerified()) {
            throw new AuthenticationException("Email already verified");
        }
        if (!user.getOtp().equals(otp)) {
            user.setOtpAttempts(user.getOtpAttempts() + 1);

            if (user.getOtpAttempts() >= 3) {
                user.setAccountLocked(true);
                userRepository.save(user);
                throw new AccountLockedException("Account locked due to invalid OTP attempts");
            }

            userRepository.save(user);
            throw new OtpException("Invalid OTP");
        }
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new OtpException("OTP expired");
        }

        user.setEmailVerified(true);
        user.setOtp(null);
        user.setOtpExpiry(null);
        user.setOtpAttempts(0);
        user.setOtpResendAttempts(0);
        user.setAccountStatus("ACTIVE"); // activate user

        Account account = accountRepository.findByUser(user);
        account.setAccountStatus("ACTIVE");
        userRepository.save(user);
        accountRepository.save(account);
    }

    @Override
    public String resendOtp(Map<String, String> request) {
        String email = request.get("email");

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("Email not found"));

        if (user.isEmailVerified()) {
            throw  new AuthenticationException("Email already verified");
        }
        if (user.isAccountLocked()) {
            throw new AccountLockedException("Account locked");
        }
        if (user.getOtpResendAttempts() >= 3) {
            throw new OtpException("Resend OTP limit reached");
        }
        String newOtp = otpGenerator.generateOtp();
        user.setOtp(newOtp);
        user.setOtpExpiry(LocalDateTime.now().plusSeconds(90));
        user.setOtpResendAttempts(user.getOtpResendAttempts()+1);

        User savedUser = userRepository.save(user);
        emailService.sendOtp(savedUser.getEmail(), newOtp);
        //smsService.sendOtp(savedUser.getPhone(),newOtp);
        return "OTP resent successfully";
    }

    @Override
    public Boolean isEmailVerified(String email) {
         return userRepository.existsByEmailAndEmailVerifiedTrue(email);
    }

}
