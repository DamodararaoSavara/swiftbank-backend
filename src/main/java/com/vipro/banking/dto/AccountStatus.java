package com.vipro.banking.dto;

public enum AccountStatus {
    INACTIVE,   // Registered but OTP not verified
    ACTIVE,     // OTP verified → can login
    BLOCKED     // Blocked by bank/admin
}
