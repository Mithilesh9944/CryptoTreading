package com.kanha.service;

import com.kanha.repository.VerificationCodeRepo;
import com.kanha.domain.Verification_Type;
import com.kanha.model.User;
import com.kanha.model.VerificationCode;
import com.kanha.utils.OTPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VerificationCodeServiceImpl implements IVerificationCodeService {
    @Autowired
    private VerificationCodeRepo verificationCodeRepo;
    @Override
    public VerificationCode sendVerificationCode(User user, Verification_Type verificationType) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(OTPUtils.generateOTP());
        verificationCode.setVerificationType(verificationType);
        verificationCode.setUser(user);
        return verificationCodeRepo.save(verificationCode);
    }

    @Override
    public VerificationCode getVerificationCodeById(Long id) throws Exception {
        Optional<VerificationCode> verificationCode =
                verificationCodeRepo.findById(id);
        if(verificationCode.isPresent()){
            return verificationCode.get();
        }
        throw new Exception("Verification code not found by Id");
    }

    @Override
    public VerificationCode getVerificationCodeByUser(Long userId) {
        return verificationCodeRepo.findByUserId(userId);
    }

    @Override
    public void deleteVerificationCodeById(VerificationCode verificationCode) {
        verificationCodeRepo.delete(verificationCode);
    }
}
