package com.example.AuthTask.service;

import com.example.AuthTask.config.JwtUtils;
import com.example.AuthTask.dao.dto.AuthRequest;
import com.example.AuthTask.dao.dto.AuthResponse;
import com.example.AuthTask.dao.dto.RegisterRequest;
import com.example.AuthTask.dao.entity.User;
import com.example.AuthTask.dao.repository.UserRepository;
import com.example.AuthTask.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public void register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        User u = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .build();
        userRepository.save(u);
    }

    public AuthResponse login(AuthRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        String token = jwtUtils.generateToken(user.getId(), user.getEmail());
        return new AuthResponse(token);
    }
}
