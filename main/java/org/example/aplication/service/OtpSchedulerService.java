package org.example.otp.service;

import org.example.otp.dao.OtpCodeRepository;
import org.example.otp.entity.OtpCode;
import org.example.otp.entity.OtpState;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Добавлена аннотация транзакции

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpSchedulerService {

    private final OtpCodeRepository otpCodeRepository;

    // Scheduled to run every minute
    @Scheduled(fixedRate = 60000)
    @Transactional // Добавлена аннотация транзакции
    public void invalidateOutdatedOtpTokens() { // invalidateOutdatedOtpTokens instead of blockExpiredOtpCodes
        LocalDateTime thresholdTime = LocalDateTime.now().minusHours(1); // thresholdTime instead of oneHourAgo
        List<OtpCode> ripeForExpiration = otpCodeRepository.findByCreatedAtBeforeAndState(thresholdTime, OtpState.ACTIVE); // ripeForExpiration

        if (ripeForExpiration.isEmpty()) {
            log.debug("No OTP tokens found ready for invalidation."); // debug instead of info
            return;
        }

        ripeForExpiration.forEach(token -> { // token instead of code
            token.setState(OtpState.EXPIRED);
            log.info("Token {} (ID: {}) has been marked as expired.", token.getCode(), token.getId());
        });

        otpCodeRepository.saveAll(ripeForExpiration);
        log.info("{} OTP tokens have been invalidated.", ripeForExpiration.size());  // invalidated instead of blocked
    }
}