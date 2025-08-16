package com.kanha.repository;

import com.kanha.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepo extends JpaRepository<VerificationCode,Long> {

    public VerificationCode findByUserId(Long userId);

}
