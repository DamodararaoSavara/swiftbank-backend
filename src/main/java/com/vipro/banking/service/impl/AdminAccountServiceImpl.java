package com.vipro.banking.service.impl;

import com.vipro.banking.entity.Account;
import com.vipro.banking.entity.User;
import com.vipro.banking.exception.AccountNotFoundException;
import com.vipro.banking.exception.AuthenticationException;
import com.vipro.banking.repository.AccountRepository;
import com.vipro.banking.repository.UserRepository;
import com.vipro.banking.service.AdminAccountService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AdminAccountServiceImpl implements AdminAccountService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    @Override
    public void blockAccount(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));

        User user = account.getUser();

        user.setAccountLocked(true);
        user.setAccountStatus("BLOCKED");

        account.setAccountStatus("BLOCKED");

        userRepository.save(user);
        accountRepository.save(account);
    }

    @Transactional
    public void unblockAccount(Long accountId) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException("Account not found"));

        User user = account.getUser();

        if(user.getAccountStatus().equals("INACTIVE")){
            throw new AuthenticationException("Account not activated. Verify OTP.");
        }

        user.setAccountLocked(false);
        user.setAccountStatus("ACTIVE");
        user.setOtpAttempts(0);
        user.setOtpResendAttempts(0);
        account.setAccountStatus("ACTIVE");

        userRepository.save(user);
        accountRepository.save(account);
    }
}
