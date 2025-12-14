package com.tengocita.auth.controller;

import com.tengocita.auth.dto.AuthResponse;
import com.tengocita.auth.dto.LoginRequest;
import com.tengocita.auth.dto.RegisterRequest;
import com.tengocita.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        AuthResponse resp = authService.register(req);
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        AuthResponse resp = authService.login(req);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        // El nombre es el email (porque lo pusiste como subject en el JWT)
        String email = authentication.getName();

        // Extraer el rol quitando el prefijo "ROLE_"
        String role = authentication.getAuthorities().stream()
                .findFirst()  // solo tienes uno
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.replace("ROLE_", ""))
                .orElse("UNKNOWN");

        // Puedes devolver un Map o crear un DTO
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("email", email);
        userInfo.put("role", role);

        return ResponseEntity.ok(userInfo);
    }
}
