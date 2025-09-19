package com.example.AuthTask.config;


import com.example.AuthTask.dao.repository.UserRepository;
import com.example.AuthTask.exception.InvalidTokenException;
import com.example.AuthTask.exception.TokenBlacklistedException;
import com.example.AuthTask.exception.TokenExpiredException;
import com.example.AuthTask.service.TokenBlacklistService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String auth = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);

            // ✅ Blacklisted token
            if (tokenBlacklistService.isBlacklisted(token)) {
                throw new TokenBlacklistedException("This token has been logged out. Please login again.");
            }

            try {
                if (!jwtUtils.validate(token)) {
                    throw new InvalidTokenException("Token is malformed or signature invalid.");
                }

                if (jwtUtils.isExpired(token)) {
                    throw new TokenExpiredException("Your session has expired.");
                }

                Long userId = jwtUtils.getUserIdFromToken(token);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ExpiredJwtException ex) {
                throw new TokenExpiredException("Your session has expired.");
            } catch (JwtException | IllegalArgumentException ex) {
                throw new InvalidTokenException("Failed to parse token: " + ex.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }

}

