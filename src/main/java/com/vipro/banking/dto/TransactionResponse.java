package com.vipro.banking.dto;

import java.time.LocalDateTime;

public record TransactionResponse(Long id,
                                  Long accountId,
                                  double amount,
                                  String transactionType,
                                   String referenceId,
                                  LocalDateTime dateTimeStamp) {
}
