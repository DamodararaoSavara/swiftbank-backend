package com.vipro.banking.service.impl;

import com.vipro.banking.dto.*;
import com.vipro.banking.entity.Account;
import com.vipro.banking.entity.Role;
import com.vipro.banking.entity.Transaction;
import com.vipro.banking.entity.User;
import com.vipro.banking.exception.AccountNotFoundException;
import com.vipro.banking.exception.AuthenticationException;
import com.vipro.banking.exception.InsufficientAmountException;
import com.vipro.banking.exception.RegisterApiException;
import com.vipro.banking.mapper.AccountMapper;
import com.vipro.banking.redis.service.IdempotencyService;
import com.vipro.banking.redis.service.OtpService;
import com.vipro.banking.redis.service.RedisLockService;
import com.vipro.banking.repository.AccountRepository;
import com.vipro.banking.repository.RoleRepository;
import com.vipro.banking.repository.TransactionRepository;
import com.vipro.banking.repository.UserRepository;
import com.vipro.banking.service.AccountServices;
//import com.vipro.banking.service.SmsService;
import com.vipro.banking.utility.OtpGenerator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountServices {
    private AccountRepository accountRepository;
    private UserRepository userRepository;
    private TransactionRepository transactionRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;
    private OtpGenerator otpGenerator;
    private EmailService emailService;
    //private SmsService smsService;
    private RedisLockService redisLockService;
    private IdempotencyService idempotencyService;
    private OtpService otpService;

    private static final String TRANSACTION_TYPE_DEPOSIT = "DEPOSIT";
    private static final String TRANSACTION_TYPE_WITHDRAW = "WITHDRAW";
    private static final String TRANSACTION_TYPE_DEBIT = "TRANSFER_DEBIT";
    private static final String TRANSACTION_TYPE_CREDIT = "TRANSFER_CREDIT";
    private static final String ACCOUNT_STATUS = "INACTIVE";
    private static final String ACCOUNT_CACHE = "account";
    private static final String ACCOUNT_TRANSACTIONS = "account-transactions";
    @Transactional
    @Override
    public String createAccount(BankRegistrationRequest registrationRequest) {
        Account saveAccount = null;
        String username = registrationRequest.getFirstName()+""+registrationRequest.getLastName();
       /* if (userRepository.existsByUsername(username)) {
            throw new RegisterApiException(HttpStatus.BAD_REQUEST, "Username already exist");
        }*/
        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new RegisterApiException(HttpStatus.BAD_REQUEST, "Email already exist");
        }
        if(!registrationRequest.getPassword().equals(registrationRequest.getConfirmPassword())){
            throw new RegisterApiException(HttpStatus.BAD_REQUEST, "Password does not match");
        }
        try {

            User user = new User();
            user.setFirstName(registrationRequest.getFirstName());
            user.setLastName(registrationRequest.getLastName());
            user.setEmail(registrationRequest.getEmail());
            user.setPhone(registrationRequest.getPhone());
            user.setDob(registrationRequest.getDob());
            user.setGender(registrationRequest.getGender());
            user.setAddress(registrationRequest.getAddress());
            user.setCity(registrationRequest.getCity());
            user.setState(registrationRequest.getState());
            user.setPostalCode(registrationRequest.getPostalCode());
            user.setCountry(registrationRequest.getCountry());
            user.setIdType(registrationRequest.getIdType());
            user.setIdNumber(registrationRequest.getIdNumber());
            user.setOccupation(registrationRequest.getOccupation());
            user.setAnnualIncome(registrationRequest.getAnnualIncome());
            user.setAccountType(registrationRequest.getAccountType());
            user.setAccountStatus(ACCOUNT_STATUS);
            user.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

            user.setUsername(username);

            String otp = otpGenerator.generateOtp();
            user.setOtp(otp);
            user.setOtpExpiry(LocalDateTime.now().plusSeconds(90));
            user.setEmailVerified(false);

            Role role = null;
            Set<Role> roleDb = new HashSet<>();
            if (username.equals("damodar") ||
                    registrationRequest.getEmail().equals("damusonu1@gmail.com")) {
                role = roleRepository.findByName("ROLE_ADMIN");
                roleDb.add(role);
                user.setRoles(roleDb);
            } else {
                role = roleRepository.findByName("ROLE_USER");
                roleDb.add(role);
                user.setRoles(roleDb);
            }
            User savedUser = userRepository.save(user);

            Account account = AccountMapper.mapToAccount(registrationRequest);
            account.setUser(savedUser);
            account.setAccountType(registrationRequest.getAccountType());
            account.setCreatedDate(LocalDateTime.now());
            account.setAccountStatus(ACCOUNT_STATUS);
            account.setBalance(0);

            saveAccount = accountRepository.save(account);
            emailService.sendOtp(savedUser.getEmail(),otp);
//            smsService.sendOtp(savedUser.getPhone(),otp);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return "OTP sent to email";
    }

    @Override
    @Cacheable(cacheNames = ACCOUNT_CACHE,key = "#accountId")
    public AccountProfileResponse getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(()->new AccountNotFoundException("Account does not exist"));
        return AccountMapper.mapToAccountDto(account);
    }

    @Override
    public List<AccountProfileResponse> getAllAccounts() {
       List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map((account->AccountMapper.mapToAccountDto(account)))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    @Caching(
            put = @CachePut(cacheNames = ACCOUNT_CACHE,key = "#accountId"),
            evict = @CacheEvict(cacheNames = ACCOUNT_TRANSACTIONS,key = "#accountId")
    )
    public AccountProfileResponse deposit(Long accountId, double amount) {
        System.out.println("UPDATE service of deposit with  ID = "+accountId);
        Account account = accountRepository.findById(accountId)
                .orElseThrow(()->new AccountNotFoundException("Account does not exist"));
        account.setBalance(account.getBalance()+amount);
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_DEPOSIT);
        transaction.setDateTimeStamp(LocalDateTime.now());
        transactionRepository.save(transaction);

        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    @Transactional
    @Caching(
            put = @CachePut(cacheNames = ACCOUNT_CACHE,key = "#accountId"),
            evict = @CacheEvict(cacheNames = ACCOUNT_TRANSACTIONS,key = "#accountId")
    )
    public AccountProfileResponse withdraw(Long accountId, double amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(()->new AccountNotFoundException("Account does not exist"));
        if(account.getBalance()<amount)
            throw new InsufficientAmountException("Insufficient balance \uD83D\uDCB8");
        account.setBalance(account.getBalance()-amount);
        Account savedAccount = accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setAmount(amount);
        transaction.setTransactionType(TRANSACTION_TYPE_WITHDRAW);
        transaction.setDateTimeStamp(LocalDateTime.now());
        transactionRepository.save(transaction);
        return AccountMapper.mapToAccountDto(savedAccount);
    }

    @Override
    @CacheEvict(cacheNames = ACCOUNT_CACHE,key = "#accountId")
    public void delete(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(()->new AccountNotFoundException("Account does not exist"));
        accountRepository.deleteById(accountId);
    }
    @Transactional
    @Override
    public void transferFunds(Long accountId, FundTransferRequest req,String requestId) {

        Account fromAccount = accountRepository.findById(accountId)
                .orElseThrow(()->new AccountNotFoundException("Sender account not found"));
        Account toAccount =  accountRepository.findById(req.toAccountId())
                .orElseThrow(()->new AccountNotFoundException("Receiver account not found"));
        User user = toAccount.getUser();
        if(user.getAccountStatus().equals("INACTIVE") || !user.isEmailVerified()){
            throw new AuthenticationException("The recipient’s account is currently inactive. " +
                    "Transactions cannot be processed at this time.");
        }
         // 🔐 OTP validation FIRST
        otpService.validateOtp(accountId,req.otp());

        if(!redisLockService.lockAccount(accountId)){
            throw new RuntimeException("Another transaction in progress");
        }
        try{
            // 🔁 Idempotency
            idempotencyService.checkDuplicate(requestId);
            if (accountId.equals(req.toAccountId())) {
                throw new IllegalArgumentException("Cannot transfer to same account");
            }

            // 💡 CREATE referenceId HERE (ONCE)
            String referenceId = UUID.randomUUID().toString();

            if(fromAccount.getBalance()< req.amount())
                throw new InsufficientAmountException("Insufficient balance \uD83D\uDCB8");
            // ✅ Debit & Credit
            fromAccount.setBalance(fromAccount.getBalance() - req.amount());
            toAccount.setBalance(toAccount.getBalance() + req.amount());
            // ✅ Save accounts
            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            // ✅ Debit transaction
            Transaction debitTx = new Transaction();
            debitTx.setAccountId(accountId);
            debitTx.setAmount(req.amount());
            debitTx.setTransactionType(TRANSACTION_TYPE_DEBIT);
            debitTx.setReferenceId(referenceId);
            debitTx.setDateTimeStamp(LocalDateTime.now());

            // ✅ Credit transaction
            Transaction creditTx = new Transaction();
            creditTx.setAccountId(req.toAccountId());
            creditTx.setAmount(req.amount());
            creditTx.setTransactionType(TRANSACTION_TYPE_CREDIT);
            creditTx.setReferenceId(referenceId);
            creditTx.setDateTimeStamp(LocalDateTime.now());

            transactionRepository.save(debitTx);
            transactionRepository.save(creditTx);
            idempotencyService.markSuccess(requestId);

        }finally {
            redisLockService.unlockAccount(accountId);
        }

    }
    @Override
    @Cacheable(cacheNames = ACCOUNT_TRANSACTIONS, key = "#accountId")
    public List<TransactionResponse> transactionsById(Long accountId){
       List<Transaction> transactions = transactionRepository.findByAccountIdOrderByDateTimeStampDesc(accountId);
      return transactions.stream().map(transaction ->matToTransactionDto(transaction))
              .collect(Collectors.toList());

    }
    private static TransactionResponse matToTransactionDto(Transaction transaction){
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getTransactionType(),
                transaction.getReferenceId(),
                transaction.getDateTimeStamp()
        );
    }
}
