package com.kyunghwan.loginskeleton.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyunghwan.loginskeleton.account.dto.AccountDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("스프링 시큐리티 설정 테스트")
public class SecurityConfigTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("리소스 적용 테스트 (css)")
    @Test
    public void getResourceCss() throws Exception {
        mockMvc.perform(get("/css/base.css"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("리소스 적용 테스트 (js)")
    @Test
    public void getResourceJs() throws Exception {
        mockMvc.perform(get("/js/base.js"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

    @DisplayName("올바른 형식으로 회원가입 요청을 보냈지만 csrf 토큰을 포함하지 않아서 실패하는 테스트")
    @Test
    public void csrfTest() throws Exception {
        String email = "email@email.com";
        String password = "password";

        AccountDTO accountDTO = AccountDTO.builder()
                .email(email)
                .password(password)
                .build();

        mockMvc.perform(post("/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTO)))
                .andDo(print())
                .andExpect(status().isForbidden())
        ;
    }

}