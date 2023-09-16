package com.example.AccountProject.controller;

import com.example.AccountProject.dto.AccountDto;
import com.example.AccountProject.dto.CreateAccount;
import com.example.AccountProject.dto.DeleteAccount;
import com.example.AccountProject.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
//실제AccountController에 MockBean으로 등록된 가짜 accountService가 주입되어 Test컨테이너에 저장.
//주입된 어플리케이션 상대로 MockMvc가 요청을 날려서 테스트 진행.
class AccountControllerTest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    //기본 스프링에 내장된 ObjectMapper
    //잭슨 : Json -> Object, Object->Json 문자열로 상호간에 변환시켜줌.
    private ObjectMapper objectMapper;

    //계좌 생성 API(AccountController) 성공 (파라미터 : 사용자 ID, 초기 잔액)
    @Test
    void successCreateAccount() throws Exception {
        //AccountDto 값
        //given 어떤 데이터가 있을 때,
        given(accountService.createAccount(anyLong(), anyLong()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());
        //when 어떤 동작을 하게 되면
        //then 어떤 결과가 나와야한다
        mockMvc.perform(post("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new CreateAccount.Request(1L, 100L)
                        )))
                .andExpect(status().isOk())
                //응답 body에 오는 값.
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value(1234567890))
                .andDo(print());
    }

    //계좌 해지 API(파라미터 : 사용자 ID, 계좌번호) 성공
    @Test
    void successDeleteAccount() throws Exception {
        //given 어떤 데이터가 있을 때,
        given(accountService.deleteAccount(anyLong(), anyString()))
                .willReturn(AccountDto.builder()
                        .userId(1L)
                        .accountNumber("1234567890")
                        .registeredAt(LocalDateTime.now())
                        .unRegisteredAt(LocalDateTime.now())
                        .build());
        //then 어떤 결과가 나와야한다
        mockMvc.perform(delete("/account")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new DeleteAccount.Request(3333L, "0987654321")
                        )))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"))
                .andDo(print());
    }


    //계좌 확인 API(파라미터 : 사용자 ID) 성공
    @Test
    void successGetAccountsByUserId() throws Exception {
        //given 어떤 데이터가 있을 때,
        List<AccountDto> accountDtos =
                Arrays.asList(
                        AccountDto.builder()
                                .accountNumber("1234567890")
                                .balance(1000L).build(),
                        AccountDto.builder()
                                .accountNumber("1111111111")
                                .balance(2000L).build(),
                        AccountDto.builder()
                                .accountNumber("2222222222")
                                .balance(3000L).build()
                );
        given(accountService.getAccountsByUserId(anyLong()))
                .willReturn(accountDtos);
        //then
        mockMvc.perform(get("/account?user_id=1"))
                .andDo(print())
                .andExpect(jsonPath("$[0].accountNumber").value("1234567890"))
                .andExpect(jsonPath("$[0].balance").value("1000"))
                .andExpect(jsonPath("$[1].accountNumber").value("1111111111"))
                .andExpect(jsonPath("$[1].balance").value("2000"))
                .andExpect(jsonPath("$[2].accountNumber").value("2222222222"))
                .andExpect(jsonPath("$[2].balance").value("3000"));
    }
}

