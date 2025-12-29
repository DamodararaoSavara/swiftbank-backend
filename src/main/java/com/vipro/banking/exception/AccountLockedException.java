package com.vipro.banking.exception;

public class AccountLockedException extends RuntimeException{
    public AccountLockedException(String message) {
        super(message);
    }
}
