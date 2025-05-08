package org.example.otp.dao;


import org.example.otp.entity.OtpConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpConfigRepository extends JpaRepository<OtpConfig, Long> {
    OtpConfig findTopByOrderByIdAsc();
}

