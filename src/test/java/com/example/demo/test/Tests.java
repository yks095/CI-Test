package com.example.demo.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class Tests {

    @DisplayName("CI Test")
    @Test
    void test_ci()  {
        String s = "Hello Java";

        assertThat(s).isEqualTo("Hello Java");
    }
}
