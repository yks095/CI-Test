package com.kyunghwan.loginskeleton.account;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyunghwan.loginskeleton.account.dto.AccountDTO;
import com.kyunghwan.loginskeleton.common.AppProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.servlet.http.HttpSession;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppProperties appProperties;

    @DisplayName("AppProperties 테스트")
    @Test
    public void propertiesTest() {
        assertThat(appProperties.getEmail()).isEqualTo("123@email.com");
        assertThat(appProperties.getPassword()).isEqualTo("password");
    }

    @DisplayName("인증 없이 메인화면에 접근하는 테스트")
    @Test
    public void getIndexAuthX() throws Exception {
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(redirectedUrl("http://localhost/sign-in"))
                .andExpect(status().is3xxRedirection())
        ;
    }

    @DisplayName("로그인 화면 조회 테스트")
    @Test
    public void getSignIn() throws Exception {
        mockMvc.perform(get("/sign-in"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-in"))
        ;
    }

    @DisplayName("회원가입 화면 조회 테스트")
    @Test
    public void getSignUp() throws Exception {
        mockMvc.perform(get("/sign-up"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("account/sign-up"))
        ;
    }

    @DisplayName("회원가입 성공 테스트")
    @Test
    public void successSignUp() throws Exception {
        String email = "email@email.com";
        String password = "password";

        AccountDTO accountDTO = AccountDTO.builder()
                .email(email)
                .password(password)
                .build();

        ResultActions resultActions = mockMvc.perform(post("/sign-up")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(accountDTO))
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isCreated())
        ;

        String string = resultActions.andReturn().getResponse().getContentAsString();
        String msg = "회원가입 성공!";
        assertThat(string).contains(msg);

        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        assertThat(email).isEqualTo(account.getEmail());
        assertThat(password).isNotEqualTo(account.getPassword());
    }

    @DisplayName("회원가입 실패 테스트")
    @ParameterizedTest(name = "#{index} : {2}")
    @MethodSource("params")
    public void failureSignUp(String email, String password, String message) throws Exception {
        AccountDTO accountDTO = AccountDTO.builder()
                .email(email)
                .password(password)
                .build();

        mockMvc.perform(post("/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(accountDTO))
                    .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    private static Stream<Arguments> params() {
        return Stream.of(
                Arguments.of(null, "password", "이메일 입력 X"),
                Arguments.of("email@email.com", null, "패스워드 입력 X"),
                Arguments.of(null, null, "이메일 입력 X, 패스워드 입력 X"),
                Arguments.of("email", "password", "이메일 형식 오류"),
                Arguments.of("email@email.com", "11", "패스워드 8자 미만"),
                Arguments.of("email@email.com", "111111111111111111111111111", "패스워드 20자 초과"),
                Arguments.of("email", "11", "이메일, 패스워드 형식 오류")
        );
    }

    @DisplayName("회원가입 실패 테스트 - 존재하는 이메일")
    @Test
    public void failureSignUpExistEmail() throws Exception {
        String email = "123@email.com";
        String password = "password";
        saveAccount(email, password);

        AccountDTO accountDTO = AccountDTO.builder()
                .email(email)
                .password(password)
                .build();

        mockMvc.perform(post("/sign-up")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(accountDTO))
                    .with(csrf()))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @DisplayName("로그인 성공 테스트")
    @Test
    public void successSignIn() throws Exception {
        String email = "123@email.com";
        String password = "password";
        saveAccount(email, password);

        ResultActions resultActions = mockMvc.perform(formLogin()
                    .loginProcessingUrl("/sign-in")
                    .user(email)
                    .password(password))
                .andDo(print())
                .andExpect(redirectedUrl("/"))
                .andExpect(status().is3xxRedirection())
        ;

        String redirectedUrl = resultActions.andReturn().getResponse().getRedirectedUrl();
        assert redirectedUrl != null;

        HttpSession session = resultActions.andReturn().getRequest().getSession();
        assert session != null;

        mockMvc.perform(get(redirectedUrl)
                    .session((MockHttpSession) session))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("name"))
                .andExpect(model().attribute("name", email))
                .andExpect(view().name("index"));
    }

    @DisplayName("로그인 실패 테스트")
    @Test
    public void failureSignIn() throws Exception {
        String email = "123@email.com";
        String password = "password";

        mockMvc.perform(formLogin()
                    .loginProcessingUrl("/sign-in")
                    .user(email)
                    .password(password))
                .andDo(print())
                .andExpect(redirectedUrl("/sign-in?error"))
                .andExpect(status().is3xxRedirection())
        ;
    }

    @DisplayName("GET \"/test\" Success")
    @Test
    public void getTest() throws Exception {
        String email = "123@email.com";
        String password = "password";
        saveAccount(email, password);

        ResultActions resultActions = mockMvc.perform(get("/test"))
                .andDo(print())
                .andExpect(redirectedUrl("http://localhost/sign-in"))
                .andExpect(status().is3xxRedirection())
        ;

        String redirectedUrl = resultActions.andReturn().getResponse().getRedirectedUrl();
        assert redirectedUrl != null;

        HttpSession session = resultActions.andReturn().getRequest().getSession();
        assert session != null;

        mockMvc.perform(post(redirectedUrl)
                    .param("username", email)
                    .param("password", password)
                    .session((MockHttpSession) session)
                    .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("http://localhost/test"))
                .andExpect(status().is3xxRedirection())
        ;
    }

    @DisplayName("회원가입 성공 테스트 - form")
    @Test
    public void successSignUpForm() throws Exception {
        String email = "123@email.com";
        String password = "password";
        accountRepository.deleteAll();

        mockMvc.perform(post("/sign-up-form")
                    .param("email", email)
                    .param("password", password)
                    .with(csrf()))
                .andDo(print())
                .andExpect(redirectedUrl("/sign-in"))
                .andExpect(status().is3xxRedirection())
        ;

        Account account = accountRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
        assertThat(account.getEmail()).isEqualTo(email);
        assertThat(account.getPassword()).isNotEqualTo(password);
    }

    @DisplayName("회원가입 실패 테스트 - form : 이메일 형식 오류")
    @Test
    public void failureSignUpForm() throws Exception {
        String email = "123";
        String password = "password";
        // 이메일 형식 오류, 이하 나머지 valid 테스트 생략
        mockMvc.perform(post("/sign-up-form")
                    .param("email", email)
                    .param("password", password)
                    .with(csrf()))
                .andDo(print())
                .andExpect(view().name("redirect:/sign-up-form"))
                .andExpect(status().is3xxRedirection())
        ;
    }

    private void saveAccount(String email, String password) {
        accountRepository.deleteAll();
        accountRepository.save(Account.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .build());
    }

    @DisplayName("로그아웃 실패 테스트")
    @Test
    public void failureLogout() throws Exception {
        mockMvc.perform(post("/logout"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @DisplayName("로그아웃 성공 테스트")
    @Test
    public void successLogout() throws Exception {
        String email = "123@email.com";
        String password = "password";
        saveAccount(email, password);

        ResultActions resultActions = mockMvc.perform(formLogin()
                    .loginProcessingUrl("/sign-in")
                    .user(email)
                    .password(password))
                .andDo(print())
                .andExpect(redirectedUrl("/"))
                .andExpect(status().is3xxRedirection())
        ;

        HttpSession session = resultActions.andReturn().getRequest().getSession();
        assert session != null;

        resultActions = mockMvc.perform(post("/logout")
                    .session((MockHttpSession) session)
                    .with(csrf()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
        ;

        String redirectedUrl = resultActions.andReturn().getResponse().getRedirectedUrl();
        assert redirectedUrl != null;

        assertThat(redirectedUrl).isEqualTo("/sign-in");
    }

}