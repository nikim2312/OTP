package org.example.otp.service;

import org.example.otp.dao.OtpCodeRepository;
import org.example.otp.dao.OtpConfigRepository;
import org.example.otp.dao.UserRepository;
import org.example.otp.entity.OtpCode;
import org.example.otp.entity.OtpConfig;
import org.example.otp.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;  // Явный импорт Collectors

@Service
@RequiredArgsConstructor
public class AdminService {

    private final OtpConfigRepository otpConfigRepository;
    private final UserRepository userRepository;
    private final OtpCodeRepository otpCodeRepository;

    public OtpConfig updateOtpConfig(OtpConfig config) {
        otpConfigRepository.deleteAll(); // гарантируем, что будет только одна запись
        return otpConfigRepository.save(config);
    }

    public List<User> getAllNonAdminUsers() {
        return userRepository.findAll()
                .stream()
                .filter(user -> !user.getRole().equalsIgnoreCase("ADMIN"))
                .toList();
    }

    public void deleteUserAndOtps(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        List<OtpCode> codes = otpCodeRepository.findAll()
                .stream()
                .filter(code -> code.getUser().getId().equals(userId))
                .toList();

        otpCodeRepository.deleteAll(codes);
        userRepository.delete(user);
    }
}
