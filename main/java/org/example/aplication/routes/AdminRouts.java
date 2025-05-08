package org.example.otp.routes;

import org.example.otp.entity.OtpConfig;
import org.example.otp.entity.User;
import org.example.otp.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
public class AdminRouts {

    private final AdminService adminService;

    @PutMapping("/conf")
    public OtpConfig updateOtpConfig(@RequestBody OtpConfig config) {
        log.info("Запрос на обновление конфигурации OTP: {}", config);
        OtpConfig updatedConfig = adminService.updateOtpConfig(config);
        log.info("Конфигурация OTP успешно обновлена: {}", updatedConfig);
        return updatedConfig;
    }

    @GetMapping("/get-users")
    public List<User> getAllNonAdminUsers() {
        log.info("Получить всех дефолт-пользователей");
        List<User> users = adminService.getAllNonAdminUsers();
        log.info("Найдено {} дефолт-пользователей", users.size());
        return users;
    }

    @DeleteMapping("/user/{id}")
    public void deleteUser(@PathVariable Long id) {
        log.warn("Запрос на удаление пользователя с ID: {}", id);
        adminService.deleteUserAndOtps(id);
        log.info("Пользователь с ID {} и его OTP коды были удалены", id);
    }
}