package com.example.demo.test;

import com.example.demo.Demo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class Tests {

    @Autowired
    ObjectMapper objectMapper;

    @DisplayName("CI Test")
    @Test
    void test_ci()  {
        String s = "Hello Java";

        assertThat(s).isEqualTo("Hello Java");
    }

    @DisplayName("ObjectMapper Test")
    @Test
    void test_ob() throws JsonProcessingException {
        Demo demo = new Demo();

        demo.setJson("test json");

        String om = objectMapper.writeValueAsString(demo);

        System.out.println(om);

        assertThat(om).isEqualTo("{\"json\":\"test json\"}");
    }

}
