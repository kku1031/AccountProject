package com.example.AccountProject.service;

import com.example.AccountProject.domain.Account;
import com.example.AccountProject.domain.AccountUser;
import com.example.AccountProject.dto.AccountDto;
import com.example.AccountProject.repository.AccountRepository;
import com.example.AccountProject.repository.AccountUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

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
}