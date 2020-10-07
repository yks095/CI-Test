package com.kyunghwan.loginskeleton.config;

import com.kyunghwan.loginskeleton.account.OAuth2UserServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final OAuth2UserServiceImpl oAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                    .ignoringAntMatchers("/h2-console/**")
                    .and()
                .headers()
                    .frameOptions()
                    .disable()
                    .and()
                .authorizeRequests()
                    .antMatchers("/sign-up", "/sign-up-form", "/h2-console/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                .formLogin()
                    .loginPage("/sign-in")
                    .permitAll()
                    .and()
                .logout()
                    .deleteCookies("JSESSIONID")
                    .logoutSuccessUrl("/sign-in")
                    .and()
                .oauth2Login()
                    // .defaultSuccessUrl("/test")
                    .userInfoEndpoint()
                    .userService(oAuth2UserService)
        ;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations())
            .antMatchers("/favicon.ico", "/resources/**", "/error");
    }

}

