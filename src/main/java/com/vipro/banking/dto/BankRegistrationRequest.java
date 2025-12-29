package com.vipro.banking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class BankRegistrationRequest {

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "Invalid phone number")
    private String phone;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    @NotBlank
    private String gender;

    @NotBlank
    private String address;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String country;

    @NotBlank
    private String idType;

    @NotBlank
    private String idNumber;

    @NotBlank
    private String occupation;

    @NotNull
    private BigDecimal annualIncome;

    @NotBlank
    private String accountType;

    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank
    private String confirmPassword;
}
