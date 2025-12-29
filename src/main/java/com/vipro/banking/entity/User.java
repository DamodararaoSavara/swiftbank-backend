package com.vipro.banking.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phone;

    private LocalDate dob;
    private String gender;

    private String address;
    private String city;
    private String state;
    private String postalCode;
    private String country;

    private String idType;
    private String idNumber;

    private String occupation;

    private BigDecimal annualIncome;

    private String accountType;

    private String accountStatus;
    @Column(nullable = false)
    private boolean emailVerified;

    private String otp;
    private LocalDateTime otpExpiry;
    private int otpAttempts;          // ❗ wrong OTP count
    private int otpResendAttempts;    // ❗ resend count

    private boolean accountLocked;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private List<Account> accounts;

}
