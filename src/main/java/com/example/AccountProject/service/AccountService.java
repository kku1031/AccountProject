package com.example.AccountProject.service;


import com.example.AccountProject.domain.Account;
import com.example.AccountProject.domain.AccountUser;
import com.example.AccountProject.dto.AccountDto;
import com.example.AccountProject.exception.AccountException;
import com.example.AccountProject.repository.AccountRepository;
import com.example.AccountProject.repository.AccountUserRepository;
import com.example.AccountProject.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static com.example.AccountProject.type.AccountStatus.IN_USE;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;

    /**
     * 사용자가 있는지 조회
     * 계좌의 번호를 생성하고
     * 계좌 저장, 그 정보를 넘김.
     *
     * @return
     */
    @Transactional
    public AccountDto createAccount(Long userId, Long initialBalance) {
        //사용자 조회
        AccountUser accountUser = accountUserRepository.findById(userId)
                //orElseThrow 값이 있으면 파라미터값 받아오고 없으면 Exception 발생.
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));

        //새로운 계좌 번호 생성
        String newAccountNumber = accountRepository.findFirstByOrderByIdDesc() //최근에 생성된 계좌
                .map(account -> (Integer.parseInt(account.getAccountNumber())) + 1 + "") //계좌가 있으면 +1 후 문자로 변경
                .orElse("1000000000"); //계좌가 없으면 최초생성

        //신규 계좌 저장.
        // builder로 account 계좌 저장-> Account(Entity) -> accountRepository에 저장 -> 그러고 나온 entity
        //-> fromEntity에 저장 -> fromEntity의 Entity -> AccountDto에 저장
        //-> Controller에서 받아서 사용
        return AccountDto.fromEntity(
                accountRepository.save(Account.builder()
                        .accountUser(accountUser)
                        .accountStatus(IN_USE)
                        .accountNumber(newAccountNumber)
                        .balance(initialBalance)
                        .registeredAt(LocalDateTime.now())
                        .build())
        );
    }

    @Transactional
    public Account getAccount(Long id) {
        if(id < 0){
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }
}