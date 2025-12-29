package com.vipro.banking.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public class RegisterApiException extends RuntimeException{
    private HttpStatus status;
    private String message;
}
