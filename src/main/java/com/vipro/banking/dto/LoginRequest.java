package com.vipro.banking.dto;

public record LoginRequest(String usernameOrEmail,
                           String password) {
}
