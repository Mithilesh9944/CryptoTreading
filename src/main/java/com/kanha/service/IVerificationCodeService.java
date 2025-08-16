package com.kanha.service;

import com.kanha.domain.Verification_Type;
import com.kanha.model.User;
import com.kanha.model.VerificationCode;

public interface IVerificationCodeService {
    VerificationCode sendVerificationCode(User user, Verification_Type verificationType);
    VerificationCode getVerificationCodeById(Long id) throws Exception;
    VerificationCode getVerificationCodeByUser(Long userId);
    void deleteVerificationCodeById(VerificationCode verificationCode);

}
