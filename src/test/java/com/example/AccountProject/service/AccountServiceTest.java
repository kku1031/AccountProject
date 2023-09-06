package com.example.AccountProject.service;

import com.example.AccountProject.domain.Account;
import com.example.AccountProject.domain.AccountUser;
import com.example.AccountProject.dto.AccountDto;
import com.example.AccountProject.exception.AccountException;
import com.example.AccountProject.repository.AccountRepository;
import com.example.AccountProject.repository.AccountUserRepository;
import com.example.AccountProject.type.AccountStatus;
import com.example.AccountProject.type.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountUserRepository accountUserRepository;

    // 2개의 Mock이 달려있는 AccountService가 생성되어 Mockito로 진행.
    @InjectMocks
    private AccountService accountService;

    //계좌 생성 API(AccountService) 성공 (파라미터 : 사용자 ID, 초기 잔액)
    @Test
    void createAccountSuccess() {
        // given: 어떤 데이터가 있을 때
        AccountUser user = AccountUser.builder()
                .id(12L)
                .name("강경구").build();
        //사용자 조회
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        //새로운 계좌 번호 생성
        given(accountRepository.findFirstByOrderByIdDesc())
                .willReturn(Optional.of(Account.builder()
                        .accountNumber("1000000012").build()));
        //신규 계좌 저장(AccountService 유저와 계좌번호 전부 저장)
        given(accountRepository.save(any()))
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000014").build());

        //ArgumentCaptor : 메서드 호출 시 전달된 인자(argument)를 캡처하고 검증하는 데 사용
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        // when: 어떤 동작을 하게 되면 (응답값)
        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        // then: 어떤 결과가 나와야 함
        // accountRepository가 1번 저장을 하고 save시 captor가 정보를 capture
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId()); // accountDto.getUserId()의 값이 12L과 일치하는지 검증
        assertEquals("1000000013", captor.getValue().getAccountNumber()); // accountDto.getAccountNumber()의 값이 "1000000013"과 일치하는지 검증
    }

    //계좌 생성 API(AccountService) 실패
    @Test
    @DisplayName("계좌정보가 없는 경우")
    void createEmptyAccount() {
        // given: 어떤 데이터가 있을 때
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("강경구").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findFirstByOrderByIdDesc())
                //데이터가 없으면
                .willReturn(Optional.empty());
        given(accountRepository.save(any()))
                //임의의 값 리턴.
                .willReturn(Account.builder()
                        .accountUser(user)
                        .accountNumber("1000000015").build());

        //실제로 저장되는 데이터는 captor 안에
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        // when: 어떤 동작을 하게 되면 (응답값)
        AccountDto accountDto = accountService.createAccount(1L, 1000L);

        // then: 어떤 결과가 나와야 함
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(15L, accountDto.getUserId());
        //초기 계좌 값 리턴.
        assertEquals("1000000000", captor.getValue().getAccountNumber());
    }

    @Test
    @DisplayName("유저가 없는 경우")
    void createAccount_UserNotFound() {
        // given: 어떤 데이터가 있을 때
        given(accountUserRepository.findById(anyLong()))
                //유저가 없는 경우, 새로운 계좌 생성 및 저장 부분은 필요가 없어서 적을 필요 없음.
                .willReturn(Optional.empty());

        // 유저가 없는 경우 AccountException에서 별도로 처리했으므로 예외 처리.
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        // then: 어떤 결과가 나와야 함
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("한 명 당 최대 계좌가 5개가 넘을 경우")
    void createAccount_maxAccountIs5() {
        //given 어떤 데이터가 있을 때,
        AccountUser user = AccountUser.builder()
                .id(15L)
                .name("강경구").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.countByAccountUser(any()))
                .willReturn(5);
        // when: 어떤 동작을 하게 되면
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.createAccount(1L, 1000L));

        // then: 어떤 결과가 나와야 함
        assertEquals(ErrorCode.MAX_ACCOUNT_PER_USER_5, exception.getErrorCode());
    }

    //계좌 해지 API 성공
    @Test
    void deleteAccountSuccess() {
        // given: 어떤 데이터가 있을 때
        AccountUser user = AccountUser.builder()
                .name("강경구").build();
        user.setId(12L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user)
                        .balance(0L)
                        .accountNumber("1000000012").build()));

        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);

        // when: 어떤 동작을 하게 되면 (응답값)
        AccountDto accountDto = accountService.deleteAccount(1L, "1234567890");

        // then: 어떤 결과가 나와야 함
        verify(accountRepository, times(1)).save(captor.capture());
        assertEquals(12L, accountDto.getUserId());
        assertEquals("1000000012", captor.getValue().getAccountNumber());
        assertEquals(AccountStatus.UNREGISTERED, captor.getValue().getAccountStatus());
    }

    //계좌 해지 API 실패
    @Test
    @DisplayName("사용자가 없는 경우")
    void deleteAccount_UserNotFound() {
        // given: 어떤 데이터가 있을 때
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.empty());
        // when: 어떤 동작을 하게 되면
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        // then: 어떤 결과가 나와야 함
        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌가 없는 경우")
    void deleteAccount_AccountNotFound() {
        // given: 어떤 데이터가 있을 때
        AccountUser user = AccountUser.builder()
                .name("강경구").build();
        user.setId(12L);
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.empty());

        // when: 어떤 동작을 하게 되면
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        // then: 어떤 결과가 나와야 함
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("사용자 아이디와 계좌 소유주가 다른 경우")
    void deleteAccountFailed_userUnMatch() {
        // given: 어떤 데이터가 있을 때
        AccountUser user1 = AccountUser.builder()
                .id(12L)
                .name("강경구").build();
        AccountUser Pikachu = AccountUser.builder()
                .id(13L)
                .name("홍길동").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user1));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(Pikachu)
                        .balance(0L)
                        .accountNumber("1000000012").build()));
        // when: 어떤 동작을 하게 되면
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        // then: 어떤 결과가 나와야 함
        assertEquals(ErrorCode.USER_ACCOUNT_UN_MATCH, exception.getErrorCode());
    }

    @Test
    @DisplayName("계좌가 이미 해지 상태인 경우")
    void deleteAccountFailed_alreadyUnregistered() {
        // given: 어떤 데이터가 있을 때
        AccountUser user1 = AccountUser.builder()
                .id(12L)
                .name("강경구").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user1));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user1)
                        .accountStatus(AccountStatus.UNREGISTERED) //이미 계좌해지됨
                        .balance(0L)
                        .accountNumber("1000000012").build()));
        // when: 어떤 동작을 하게 되면
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        // then: 어떤 결과가 나와야 함
        assertEquals(ErrorCode.ACCOUNT_ALREADY_UNREGISTERED, exception.getErrorCode());
    }

    @Test
    @DisplayName("잔액이 있는 경우 실패 응답")
    void deleteAccountFailed_balanceNotEmpty() {
        // given: 어떤 데이터가 있을 때
        AccountUser user1 = AccountUser.builder()
                .id(12L)
                .name("강경구").build();
        given(accountUserRepository.findById(anyLong()))
                .willReturn(Optional.of(user1));
        given(accountRepository.findByAccountNumber(anyString()))
                .willReturn(Optional.of(Account.builder()
                        .accountUser(user1)
                        .balance(100L)                 //잔액 100원 남음.
                        .accountNumber("1000000012").build()));
        // when: 어떤 동작을 하게 되면
        AccountException exception = assertThrows(AccountException.class,
                () -> accountService.deleteAccount(1L, "1234567890"));

        // then: 어떤 결과가 나와야 함
        assertEquals(ErrorCode.BALANCE_NOT_EMPTY, exception.getErrorCode());
    }
}
