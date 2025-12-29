package com.vipro.banking.config;

import com.vipro.banking.entity.Account;
import com.vipro.banking.exception.AccountNotFoundException;
import com.vipro.banking.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class AccountSecurity {
    @Autowired
    private AccountRepository accountRepository;

    public boolean canAccessAccount(Authentication authentication, Long accountId) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String loggedUser = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(e -> e.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) return true;
        return accountOwner(loggedUser, accountId);
    }

    private boolean accountOwner(String loggedUser, Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(
                () -> new AccountNotFoundException("Account does not exist"));

        String username = account.getUser().getUsername();
        String email = account.getUser().getEmail();
        return loggedUser.equals(username) || loggedUser.equals(email);
    }
}
