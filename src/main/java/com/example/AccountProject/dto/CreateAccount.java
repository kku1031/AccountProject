package com.example.AccountProject.dto;

import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class CreateAccount {


    @Getter
    @Setter
    public static class Request {
        //Valid 유효성 검사 지정.
        @NotNull //userId는 필수값
        @Min(1)  //회원가입 후 0인 값은 없으니 1로 달아줌
        private Long userId;

        @NotNull
        @Min(100) //초기 계좌잔액(100원 이상 필요)
        private Long initialBalance;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor //빌더가 들어간 객체를 상속 받을 때 ALL,NoArgsConstructor 써야 문제 없음
    @Builder
    public static class Response {
        private Long userId;
        private String accountNumber;
        private LocalDateTime registeredAt;

        //@PostMapping("/account")에서 필요한 Response정보만 뽑아오기 위한 메소드.
        public static Response from(AccountDto accountDto) {
            return Response.builder()
                    .userId(accountDto.getUserId())
                    .accountNumber(accountDto.getAccountNumber())
                    .registeredAt(accountDto.getRegisteredAt())
                    .build();
        }
    }
}
