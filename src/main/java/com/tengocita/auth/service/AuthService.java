package com.tengocita.auth.service;


import com.tengocita.auth.dto.AuthResponse;
import com.tengocita.auth.dto.LoginRequest;
import com.tengocita.auth.dto.RegisterRequest;
import com.tengocita.auth.model.User;
import com.tengocita.auth.repository.UserRepository;
import com.tengocita.auth.security.JwtUtil.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User u = User.builder()
                .email(req.getEmail())
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .role(req.getRole())
                .active(true)
                .build();

        userRepository.save(u);
        String token = jwtUtil.generateToken(u.getEmail(), u.getRole().name());
        return new AuthResponse(token);
    }

    public AuthResponse login(LoginRequest req) {
        User u = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(u.getEmail(), u.getRole().name());
        return new AuthResponse(token);
    }
}
