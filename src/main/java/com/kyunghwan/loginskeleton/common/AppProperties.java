package com.kyunghwan.loginskeleton.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotEmpty;

@Component
@ConfigurationProperties(prefix = "test")
@Getter @Setter
public class AppProperties {

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

}
