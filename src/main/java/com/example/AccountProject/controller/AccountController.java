package com.example.AccountProject.controller;


import com.example.AccountProject.domain.Account;
import com.example.AccountProject.domain.AccountInfo;
import com.example.AccountProject.dto.CreateAccount;
import com.example.AccountProject.dto.DeleteAccount;
import com.example.AccountProject.service.AccountService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    //계좌 생성 API(파라미터 : 사용자 ID, 초기 잔액)
    @PostMapping("/account")
    public CreateAccount.Response createAccount(
            @RequestBody @Valid CreateAccount.Request request
    ) {
        //AccountDto : serverice와 Controller 사이에서 통신 연결
        return CreateAccount.Response.from(
                accountService.createAccount(
                    request.getUserId(),
                    request.getInitialBalance()
            )
        );
    }

    //계좌 해지 API(파라미터 : 사용자 ID, 계좌번호)
    @DeleteMapping("/account")
    public DeleteAccount.Response deleteAccount(
            @RequestBody @Valid DeleteAccount.Request request
    ) {
        return DeleteAccount.Response.from(
                accountService.deleteAccount(
                        request.getUserId(),
                        request.getAccountNumber()
                )
        );
    }

    //계좌 확인 API(파라미터 : 사용자 ID)
    //Service에서 List<AccountDto> 타입으로 변환된걸 List<AccountInfo>로 넣어줘야함.
    @GetMapping("/account")
    public List<AccountInfo> getAccountsByUserId(
            @RequestParam("user_id") Long userId
    ) {
        return accountService.getAccountsByUserId(userId)
                .stream().map(accountDto -> AccountInfo.builder()
                        .accountNumber(accountDto.getAccountNumber())
                        .balance(accountDto.getBalance())
                        .build())
                .collect(Collectors.toList());
    }


    @GetMapping("/account/{id}")
    public Account getAccount(
            @PathVariable Long id){
        return accountService.getAccount(id);
    }
}
