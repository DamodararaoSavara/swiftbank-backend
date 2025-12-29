package com.vipro.banking.dto;

public record FundTransferRequest(Long toAccountId,
                                  double amount,
                                  String otp) {
}
