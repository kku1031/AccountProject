package com.example.AccountProject.controller;

import com.example.AccountProject.dto.AccountDto;
import com.example.AccountProject.dto.CreateAccount;
import com.example.AccountProject.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    //계좌 생성 API 성공 (파라미터 : 사용자 ID, 초기 잔액)
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
}