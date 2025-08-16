package com.kanha.service;

import com.kanha.repository.TwoFactorOTPRepo;
import com.kanha.model.TwoFactorOTP;
import com.kanha.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TwoFactorOTPServiceImpl implements ITwoFactorOTPService {
    @Autowired
    TwoFactorOTPRepo twoFactorOTPRepo;
    @Override
    public TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt) {
        UUID uuid = UUID.randomUUID();

        String id = uuid.toString();
        TwoFactorOTP twoFactorOTP = new TwoFactorOTP();
        twoFactorOTP.setOtp(otp);
        twoFactorOTP.setId(id);
        twoFactorOTP.setUser(user);
        twoFactorOTP.setJwt(jwt);

        return twoFactorOTPRepo.save(twoFactorOTP);
    }

    @Override
    public TwoFactorOTP findByUser(Long userId) {
        return twoFactorOTPRepo.findByUserId(userId);
    }

    @Override
    public TwoFactorOTP findById(String id) {
        Optional<TwoFactorOTP> otp = twoFactorOTPRepo.findById(id);
        return otp.orElse(null);
    }

    @Override
    public boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP, String otp) {
        return twoFactorOTP.getOtp().equals(otp);
    }

    @Override
    public void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP) {
        twoFactorOTPRepo.delete(twoFactorOTP);
    }
}
