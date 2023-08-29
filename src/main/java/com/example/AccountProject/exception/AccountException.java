package com.example.AccountProject.exception;

import com.example.AccountProject.type.ErrorCode;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountException extends RuntimeException{

    private ErrorCode errorCode;
    private String errorMessage;

    //type -> ErrorCode의 description을 받기 위한 생성자
    public AccountException(ErrorCode errorCode) {
        this.errorCode = errorCode;
        this.errorMessage = errorCode.getDescription();
    }
}
