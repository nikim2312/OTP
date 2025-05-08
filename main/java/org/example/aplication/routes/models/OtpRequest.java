package org.example.otp.routes.models;

import lombok.Data;

@Data
public class OtpRequest {
    private String operationId;
    private String deliveryType; // "EMAIL", "SMS", "FILE"
}
