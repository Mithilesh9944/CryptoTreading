package com.kanha.service;

import com.kanha.repository.ForgotPasswordRepo;
import com.kanha.domain.Verification_Type;
import com.kanha.model.ForgotPasswordToken;
import com.kanha.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForgotPasswordImpl implements IForgotPasswordService {
    @Autowired
    private ForgotPasswordRepo forgotPasswordRepo;
    @Override
    public ForgotPasswordToken createToken(User user, String id, String otp, Verification_Type verificationType, String sendTo) {
        ForgotPasswordToken forgotPasswordToken = new ForgotPasswordToken();
        forgotPasswordToken.setUser(user);
        forgotPasswordToken.setOtp(otp);
        forgotPasswordToken.setSendTo(sendTo);
        forgotPasswordToken.setVerificationType(verificationType);
        forgotPasswordToken.setId(id);

        return forgotPasswordRepo.save(forgotPasswordToken);
    }

    @Override
    public ForgotPasswordToken findById(String id) {
        Optional<ForgotPasswordToken> token = forgotPasswordRepo.findById(id);
        return token.orElse(null);
    }

    @Override
    public ForgotPasswordToken findByUser(Long userId) {
        return forgotPasswordRepo.findByUserId(userId);
    }

    @Override
    public void deleteToken(ForgotPasswordToken token) {
        forgotPasswordRepo.delete(token);
    }
}
