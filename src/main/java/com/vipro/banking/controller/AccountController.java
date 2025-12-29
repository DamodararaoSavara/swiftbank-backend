package com.vipro.banking.controller;

import com.vipro.banking.dto.AccountProfileResponse;
import com.vipro.banking.dto.BankRegistrationRequest;
import com.vipro.banking.dto.FundTransferRequest;
import com.vipro.banking.dto.TransactionResponse;
import com.vipro.banking.redis.service.OtpService;
import com.vipro.banking.service.AccountServices;
import com.vipro.banking.service.impl.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/account")
@AllArgsConstructor
public class AccountController {

    private AccountServices accountServices;
    private EmailService emailService;
    private OtpService otpService;
    @PostMapping
    public ResponseEntity<String> createAccount(@RequestBody BankRegistrationRequest registrationRequest){
        String account = accountServices.createAccount(registrationRequest);
      return ResponseEntity.ok("OTP sent to email");
    }

    @GetMapping("/{accountId}")
    @PreAuthorize("@accountSecurity.canAccessAccount(authentication,#accountId)")
    public ResponseEntity<AccountProfileResponse> getAccountById(@PathVariable Long accountId){
        AccountProfileResponse account = accountServices.getAccountById(accountId);
        return ResponseEntity.ok(account);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AccountProfileResponse>> getAllAccounts(){
       List<AccountProfileResponse> accounts =  accountServices.getAllAccounts();
       return ResponseEntity.ok(accounts);
    }
    @PutMapping("/{accountId}/deposit")
    @PreAuthorize("@accountSecurity.canAccessAccount(authentication, #accountId)")
    public ResponseEntity<AccountProfileResponse> deposit(@PathVariable Long accountId,
                                                          @RequestBody Map<String,Double> request){
       double amount = request.get("amount");
        AccountProfileResponse savedAccount =  accountServices.deposit(accountId,amount);
        return ResponseEntity.ok(savedAccount);
    }

    @PutMapping("/{accountId}/withdraw")
    @PreAuthorize("@accountSecurity.canAccessAccount(authentication, #accountId)")
    public ResponseEntity<AccountProfileResponse> withdraw(@PathVariable Long accountId,
                                                           @RequestBody Map<String,Double> request){
        double amount = request.get("amount");
        AccountProfileResponse savedAccount =  accountServices.withdraw(accountId,amount);
        return ResponseEntity.ok(savedAccount);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        accountServices.delete(id);
        return ResponseEntity.ok("Account deleted successfully");
    }
    @PostMapping("/{accountId}/transfer")
    @PreAuthorize("@accountSecurity.canAccessAccount(authentication, #accountId)")
    public ResponseEntity<String> transferFund(@PathVariable Long accountId ,
                                               @RequestBody FundTransferRequest fundTransferRequest,
                                               @RequestHeader(value = "X-Request-Id",required = false) String requestId){
        if(requestId==null || requestId.isBlank()){
            requestId = UUID.randomUUID().toString();
        }
        accountServices.transferFunds(accountId, fundTransferRequest,requestId);
        return ResponseEntity.ok("Transfer funds successfully");
    }

    @GetMapping("/{accountId}/transactions")
    @PreAuthorize("@accountSecurity.canAccessAccount(authentication, #accountId)")
    public ResponseEntity<List<TransactionResponse>> transactionsById(@PathVariable Long accountId){
        List<TransactionResponse> transactions = accountServices.transactionsById(accountId);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping("/{accountId}/transfer/otp")
    @PreAuthorize("@accountSecurity.canAccessAccount(authentication, #accountId)")
    public ResponseEntity<String> sendTransferOtp(@PathVariable Long accountId) {
        AccountProfileResponse account = accountServices.getAccountById(accountId);
        String otp = otpService.generateOtp(accountId);
        // TODO: send via email/SMS
        emailService.sendOtp(account.email(),otp);
        System.out.println("Transfer OTP: " + otp);
        return ResponseEntity.ok("OTP sent successfully");
    }
}
