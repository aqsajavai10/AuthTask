package com.example.AuthTask.controller;
import com.example.AuthTask.dao.dto.AuthResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.AuthTask.dao.dto.AuthRequest;
import com.example.AuthTask.dao.dto.RegisterRequest;
import com.example.AuthTask.dao.entity.User;
import com.example.AuthTask.dao.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Transactional // rollback DB after each test so state is clean
@AutoConfigureMockMvc
// hardcoded since during mvn test Spring still sees no JWT_SECRET
@SpringBootTest(properties = {
        "jwt.secret=8F!sD3k@9pLzQ1r^2tVbX6e*GzN0wM4kYpR!aB$7hJqEoU%Z"

})

class AuthControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    void register_then_ok() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setEmail("test" + System.currentTimeMillis() + "@example.com"); // unique email
        req.setPassword("pass123");
        req.setName("Test One");

        mvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        assertThat(userRepository.existsByEmail(req.getEmail())).isTrue();
    }

    @Test
    void login_then_get_token() throws Exception {
        String uniqueEmail = "login" + System.currentTimeMillis() + "@example.com";

        // create and save user
        var user = new com.example.AuthTask.dao.entity.User();
        user.setEmail(uniqueEmail);
        user.setPassword(passwordEncoder.encode("pass123"));
        user.setName("Login Test");
        userRepository.save(user);

        // login request
        AuthRequest req = new AuthRequest();
        req.setEmail(uniqueEmail);
        req.setPassword("pass123");

        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists()) // ✅ changed to accessToken
                .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }


}
