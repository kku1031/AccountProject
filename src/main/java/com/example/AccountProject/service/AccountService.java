package com.example.AccountProject.service;


import com.example.AccountProject.domain.Account;
import com.example.AccountProject.domain.AccountUser;
import com.example.AccountProject.dto.AccountDto;
import com.example.AccountProject.exception.AccountException;
import com.example.AccountProject.repository.AccountRepository;
import com.example.AccountProject.repository.AccountUserRepository;
import com.example.AccountProject.type.AccountStatus;
import com.example.AccountProject.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static com.example.AccountProject.type.AccountStatus.IN_USE;
import static com.example.AccountProject.type.ErrorCode.*;

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

        //계좌 개수 최대 5개 메소드 호출
        validateCreateAccount(accountUser);

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

    //계좌 개수 최대 5개로 제한
    private void validateCreateAccount(AccountUser accountUser) {
        if(accountRepository.countByAccountUser(accountUser) >= 5) {
            throw new AccountException(ErrorCode.MAX_ACCOUNT_PER_USER_5);
        }
    }

    //계좌 해지 API
    @Transactional
    public AccountDto deleteAccount(Long userId, String accountNumber) {
        AccountUser accountUser = accountUserRepository.findById(userId)                //유저 아이디 조회
                .orElseThrow(() -> new AccountException(ErrorCode.USER_NOT_FOUND));     //사용자가 없을 때
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountException(ErrorCode.ACCOUNT_NOT_FOUND));  //계좌가 없을 때,
        validateDeleteAccount(accountUser, account);

        //계좌 해지 후 -> 상태 업데이트, 시간등록
        account.setAccountStatus(AccountStatus.UNREGISTERED);
        account.setUnRegisteredAt(LocalDateTime.now());

        //없어도 동작 -> AccountServiceTest위해 작성.
        accountRepository.save(account);

        return AccountDto.fromEntity(account);
    }

    //계좌 해지가 불가능한 경우
    private void validateDeleteAccount(AccountUser accountUser, Account account) {
        if (accountUser.getId() != account.getAccountUser().getId()) {
            throw new AccountException(USER_ACCOUNT_UN_MATCH);            //사용자 아이디, 계좌 소유주가 다른 경우
        }
        if (account.getAccountStatus() == AccountStatus.UNREGISTERED) {
            throw new AccountException(ACCOUNT_ALREADY_UNREGISTERED);     //계좌가 이미 해지 상태인 경우
        }
        if (account.getBalance() > 0) {
            throw new AccountException(BALANCE_NOT_EMPTY);                //잔액이 있는 경우 실패 응답
        }
    }


    @Transactional
    public Account getAccount(Long id) {
        if(id < 0){
            throw new RuntimeException("Minus");
        }
        return accountRepository.findById(id).get();
    }
}
