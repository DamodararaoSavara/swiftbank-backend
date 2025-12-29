package com.vipro.banking.controller;

import com.vipro.banking.service.AdminAccountService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminAccountController {
    private final AdminAccountService adminAccountService;

    @PutMapping("/block/{accountId}")
    public ResponseEntity<String> blockAccount(
            @PathVariable Long accountId) {

        adminAccountService.blockAccount(accountId);
        return ResponseEntity.ok("Account blocked successfully");
    }

    @PutMapping("/unblock/{accountId}")
    public ResponseEntity<String> unblockAccount(
            @PathVariable Long accountId) {

        adminAccountService.unblockAccount(accountId);
        return ResponseEntity.ok("Account unblocked successfully");
    }
}
