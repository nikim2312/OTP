package org.example.otp.routes.models;

import lombok.Data;

@Data
public class OtpValidationRequest {
    private String operationId;
    private String code;
}
