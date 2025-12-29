package com.vipro.banking.repository;

import com.vipro.banking.entity.Account;
import com.vipro.banking.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account,Long> {
  Account findByUser(User user);
}
