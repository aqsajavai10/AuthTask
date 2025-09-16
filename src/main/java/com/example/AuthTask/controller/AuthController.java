package com.example.AuthTask.controller;


import com.example.AuthTask.dao.dto.AuthRequest;
import com.example.AuthTask.dao.dto.AuthResponse;
import com.example.AuthTask.dao.dto.RegisterRequest;
import com.example.AuthTask.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        AuthResponse resp = authService.login(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // Stateless JWT: logout can be client-side (drop token). Optionally implement blacklist.
        return ResponseEntity.ok().build();
    }
}

