package com.vipro.banking.service;

public interface AdminAccountService {
    public void blockAccount(Long accountId);
    public void unblockAccount(Long accountId);
}
