package org.example.otp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "otp_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "otp_length", nullable = false)
    private Integer otpLength;

    // Можно добавить другие параметры конфигурации OTP
}
