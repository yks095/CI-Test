package com.kyunghwan.loginskeleton.account.dto;

import com.kyunghwan.loginskeleton.account.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@RequiredArgsConstructor
@Component
public class AccountDTOValidator implements Validator {

    private final AccountRepository accountRepository;


    @Override
    public boolean supports(Class<?> clazz) {
        return clazz.isAssignableFrom(AccountDTO.class);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AccountDTO accountDTO = (AccountDTO) target;
        if (accountRepository.existsByEmail(accountDTO.getEmail())) {
            errors.rejectValue("email", "invalid.email", new Object[]{accountDTO.getEmail()}, "사용중인 이메일입니다.");
        }
    }

}
