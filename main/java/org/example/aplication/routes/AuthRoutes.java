package org.example.otp.routes;

import org.example.otp.routes.models.AuthRequest;
import org.example.otp.routes.models.AuthResponse;
import org.example.otp.routes.models.RegisterRequest;
import org.example.otp.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthRoutes {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        log.info("Попытка регистрации: email={}, username={}", request.getEmail(), request.getLogin());
        AuthResponse response = authService.register(request);
        log.info("Регистрация завершена успешно: {}", response);
        return response;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        log.info("Запрос на вход: login={}", request.getLogin());
        AuthResponse response = authService.authenticate(request);
        log.info("Вход выполнен успешно: {}", response);
        return response;
    }
}