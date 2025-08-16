package com.kanha.service;

import com.kanha.domain.Verification_Type;
import com.kanha.model.ForgotPasswordToken;
import com.kanha.model.User;

public interface IForgotPasswordService {
    ForgotPasswordToken createToken(User user,
                                    String id, String otp,
                                    Verification_Type verificationType,String sendTo);

    ForgotPasswordToken findById(String id);

    ForgotPasswordToken findByUser(Long userId);

    void deleteToken(ForgotPasswordToken token);
}
