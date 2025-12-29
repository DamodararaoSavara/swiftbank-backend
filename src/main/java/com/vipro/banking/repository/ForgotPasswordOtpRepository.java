package com.vipro.banking.repository;

import com.vipro.banking.entity.ForgotPasswordOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ForgotPasswordOtpRepository extends JpaRepository<ForgotPasswordOtp, Long> {

    Optional<ForgotPasswordOtp> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("DELETE FROM ForgotPasswordOtp o WHERE o.email = :email")
    void deleteByEmail(@Param("email") String email);
}
