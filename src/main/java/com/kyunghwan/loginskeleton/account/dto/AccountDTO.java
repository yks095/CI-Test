package com.kyunghwan.loginskeleton.account.dto;

import com.kyunghwan.loginskeleton.account.Account;
import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Getter @Setter
@Builder
public class AccountDTO {

    @NotBlank
    @Email
    public String email;

    @NotBlank
    @Length(min = 8, max = 20)
    public String password;

    public Account toEntity(String pwd) {
        return Account.builder()
                .email(this.email)
                .password(pwd)
                .build();
    }

}
