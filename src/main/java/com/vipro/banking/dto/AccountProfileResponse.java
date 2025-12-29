package com.vipro.banking.dto;

import java.time.LocalDateTime;

public record AccountProfileResponse(Long accountId,
                                     String username,
                                     String email,
                                     String phone,
                                     String accountType,
                                     String accountStatus,
                                     double balance,
                                     LocalDateTime createdAt) {
}
