package com.example.demo.test;

import com.example.demo.Demo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@SpringBootTest
public class Tests {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

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

    @DisplayName("Controller Test")
    @Test
    void controller_test()  throws Exception    {
        mockMvc.perform(get("/api"))
                .andDo(print())
                .andExpect(status().isOk())
        ;
    }

}
