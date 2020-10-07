package com.kyunghwan.loginskeleton.account;

import com.kyunghwan.loginskeleton.account.dto.AccountDTO;
import com.kyunghwan.loginskeleton.account.dto.AccountDTOValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
public class AccountController {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountDTOValidator accountDTOValidator;

    @InitBinder("accountDTO")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(accountDTOValidator);
    }

    @GetMapping("/")
    public String getMain(Model model, @AuthAccount Account account) {
        model.addAttribute("name", account.getEmail());
        return "index";
    }

    @GetMapping("/sign-in")
    public String getSignIn() {
        return "account/sign-in";
    }

    @GetMapping("/sign-up")
    public String getSignUp() {
        return "account/sign-up";
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@RequestBody @Valid AccountDTO accountDTO, Errors errors) {
        if (errors.hasErrors()) {
            return new ResponseEntity<>("error", HttpStatus.BAD_REQUEST);
        }

        accountRepository.save(accountDTO.toEntity(passwordEncoder.encode(accountDTO.getPassword())));
        return new ResponseEntity<>("{\"msg\" : \"회원가입 성공!\"}", HttpStatus.CREATED);
    }

    @GetMapping("/sign-up-form")
    public String getSignUpForm(Model model) {
        model.addAttribute(new AccountDTO());
        return "account/sign-up-form";
    }

    @PostMapping("/sign-up-form")
    public String signUpForm(@Valid AccountDTO accountDTO, Errors errors) {
        if (errors.hasErrors()) return "redirect:/sign-up-form";
        accountRepository.save(accountDTO.toEntity(passwordEncoder.encode(accountDTO.getPassword())));
        return "redirect:/sign-in";
    }

    @GetMapping("/test")
    public String getTest(Model model, @AuthAccount Account account) {
        model.addAttribute("name", account.getEmail());
        return "test";
    }

}
