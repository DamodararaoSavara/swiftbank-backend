package com.vipro.banking.dto;

public record RegisterRequest(String name,
                              String username,
                              String email,
                              String password) {
}
