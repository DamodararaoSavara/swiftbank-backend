package com.vipro.banking.mapper;

import com.vipro.banking.dto.AccountProfileResponse;
import com.vipro.banking.dto.BankRegistrationRequest;
import com.vipro.banking.entity.Account;
import com.vipro.banking.entity.User;

public class AccountMapper {

    public static Account mapToAccount(BankRegistrationRequest registrationRequest){
        Account account = new Account();
        //account.setId(registrationRequest.getI);
        account.setAccountType(registrationRequest.getAccountType());
        //account.setBalance(registrationRequest.getB());
        User user = new User();
        //user.setId(accountInfoDto.id());
        //user.setName(accountInfoDto.name());
        user.setUsername(registrationRequest.getFirstName()+""+registrationRequest.getLastName());
        user.setEmail(registrationRequest.getEmail());
        account.setUser(user);
        return account;
    }
    public static AccountProfileResponse mapToAccountDto(Account account){
        return new AccountProfileResponse(
                account.getId(),
                account.getUser().getUsername(),
                account.getUser().getEmail(),
                account.getUser().getPhone(),
                account.getAccountType(),
                account.getAccountStatus(),
                account.getBalance(),
                account.getCreatedDate()
        );
    }
}
