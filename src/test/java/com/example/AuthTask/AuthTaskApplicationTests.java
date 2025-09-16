package com.example.AuthTask;

import com.example.AuthTask.dao.dto.RegisterRequest;
import com.example.AuthTask.dao.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class AuthTaskApplicationTests {


    @Autowired
    MockMvc mvc;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ObjectMapper mapper;

    @Test
    public void register_then_ok() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test1@example.com");
        req.setPassword("pass123");
        req.setName("Test One");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        assertThat(userRepository.existsByEmail("test1@example.com"), org.hamcrest.CoreMatchers.is(true));
    }

}

