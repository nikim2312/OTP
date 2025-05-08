package org.example.otp.routes;


import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.example.otp.service.OtpService;
import org.example.otp.routes.models.OtpRequest;
import org.example.otp.routes.models.OtpValidationRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserServiceRoutes {

    private final OtpService otpService;

    @PostMapping("/generate-otpcode")
    public String generateOtp(@RequestBody OtpRequest request) {
        log.info("Генерация кода otp: operationId={}", request);
        String otp = otpService.initiateOtpSequence(request);
        log.info("OTP-код успешно сгенерирован для operationId={}", request);
        return otp;
    }

    @PostMapping("/validate-otp")
    public boolean validateOtp(@RequestBody OtpValidationRequest request) {
        log.info("Запрос на валидацию OTP: operationId={}", request);
        boolean isValid = otpService.verifyOtp(request);
        if (isValid) {
            log.info("OTP успешно верифицирован: operationId={}", request);
        } else {
            log.warn("Ошибка верификации OTP: operationId={}", request);
        }
        return isValid;
    }
}