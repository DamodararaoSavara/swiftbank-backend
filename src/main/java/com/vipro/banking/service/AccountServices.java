package com.vipro.banking.service;

import com.vipro.banking.dto.*;

import java.util.List;

public interface AccountServices {
  String createAccount(BankRegistrationRequest registrationRequest);
  AccountProfileResponse getAccountById(Long accountId);
  List<AccountProfileResponse> getAllAccounts();
  AccountProfileResponse deposit(Long accountId, double amount);
  AccountProfileResponse withdraw(Long accountId, double amount);
  void delete(Long accountId);
  void transferFunds(Long accountId, FundTransferRequest fundTransferRequest,String requestId);
  List<TransactionResponse> transactionsById(Long accountId);
}
