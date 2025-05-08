package org.example.otp.service;

import org.example.otp.dao.OtpCodeRepository;
import org.example.otp.dao.OtpConfigRepository;
import org.example.otp.dao.UserRepository;
import org.example.otp.entity.OtpCode;
import org.example.otp.entity.OtpConfig;
import org.example.otp.entity.OtpState;
import org.example.otp.entity.User;
import org.example.otp.routes.models.OtpRequest;
import org.example.otp.routes.models.OtpValidationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.context.SecurityContextHolder;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpService {

    private final OtpCodeRepository otpCodeRepository;
    private final OtpConfigRepository otpConfigRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final SmppService smppService;
    private final TelegramService telegramService;
    private final UserService userService;

    private static final SecureRandom entropySource = new SecureRandom(); // entropySource вместо random

    public String initiateOtpSequence(OtpRequest requestForm) {  // initiateOtpSequence вместо generateOtp, requestForm вместо request
        OtpConfig configuration = otpConfigRepository.findTopByOrderByIdAsc();  // configuration вместо config
        int codeLength = (configuration != null) ? configuration.getOtpLength() : 6; // codeLength вместо length
        String generatedCode = assembleNumericCode(codeLength); // assembleNumericCode вместо generateNumericCode

        User authenticatedUser = fetchCurrentUser();  // fetchCurrentUser вместо getCurrentUser

        OtpCode otpRecord = OtpCode.builder()  // otpRecord вместо otp
                .code(generatedCode)
                .operationId(requestForm.getOperationId())
                .createdAt(LocalDateTime.now())
                .user(authenticatedUser)
                .state(OtpState.ACTIVE)
                .build();

        otpCodeRepository.save(otpRecord);

        log.info("Generated OTP {} for user {} with operationId {}", generatedCode, authenticatedUser.getLogin(), requestForm.getOperationId()); // Добавлено логирование

        // "Code transmission" segment
        switch (requestForm.getDeliveryType().toUpperCase()) {  // requestForm вместо request
            case "FILE" -> persistToFile(authenticatedUser.getLogin(), generatedCode); // persistToFile вместо saveToFile
            case "EMAIL" -> emailService.sendEmail(authenticatedUser, generatedCode);
            case "SMS" -> smppService.sendSms(authenticatedUser.getPhoneNumber(), generatedCode);
            case "TELEGRAM" -> telegramService.sendCode(authenticatedUser.getTelegram(), generatedCode);
            default -> throw new IllegalArgumentException("Unsupported delivery mechanism: " + requestForm.getDeliveryType()); // Более информативное исключение
        }

        return generatedCode;  // Возврат сгенерированного кода
    }

    public boolean verifyOtp(OtpValidationRequest validationAttempt) { // verifyOtp вместо validateOtp, validationAttempt вместо request
        Optional<OtpCode> potentialOtp = otpCodeRepository.findByOperationId(validationAttempt.getOperationId());  // potentialOtp вместо otpOptional
        if (potentialOtp.isEmpty()) {
            log.warn("Attempt to validate OTP with non-existent operationId: {}", validationAttempt.getOperationId());
            return false;
        }

        OtpCode actualOtp = potentialOtp.get();

        if (actualOtp.getState() != OtpState.ACTIVE) {
            log.warn("Attempt to validate inactive OTP with operationId: {}", validationAttempt.getOperationId());
            return false;
        }

        actualOtp.setState(OtpState.USED);
        otpCodeRepository.save(actualOtp);

        log.info("OTP validation attempt for operationId: {} - Result: {}", validationAttempt.getOperationId(), actualOtp.getCode().equals(validationAttempt.getCode()));

        // Optional: Check TTL (Time To Live)
        return actualOtp.getCode().equals(validationAttempt.getCode());
    }

    private String assembleNumericCode(int codeLength) {  // assembleNumericCode вместо generateNumericCode
        StringBuilder codeBuilder = new StringBuilder(codeLength);  // codeBuilder вместо sb
        for (int i = 0; i < codeLength; i++) {
            codeBuilder.append(entropySource.nextInt(10));  // entropySource вместо random
        }
        return codeBuilder.toString();
    }

    private void persistToFile(String userIdentifier, String otpValue) { // persistToFile вместо saveToFile
        String filename = "otp_" + userIdentifier + ".txt";
        try (FileWriter fileWriter = new FileWriter(filename)) { // fileWriter вместо writer
            fileWriter.write("OTP Value: " + otpValue);  // otpValue вместо code
            log.info("OTP saved to file: {}", filename);
        } catch (IOException ioException) {
            log.error("Error persisting OTP to file {}: {}", filename, ioException.getMessage(), ioException);  // Более подробный лог
            throw new RuntimeException("Error persisting OTP to file", ioException); // Передаем исключение для трассировки
        }
    }

    private User fetchCurrentUser() {  // fetchCurrentUser вместо getCurrentUser
        String userLogin = SecurityContextHolder.getContext().getAuthentication().getName(); // userLogin вместо login
        return userService.findByLogin(userLogin);
    }
}