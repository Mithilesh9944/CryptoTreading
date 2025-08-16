package com.kanha.service;

import com.kanha.model.TwoFactorAuth;
import com.kanha.repository.UserRepo;
import com.kanha.config.JwtProvider;
import com.kanha.domain.Verification_Type;
import com.kanha.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepo userRepo;
    @Override
    public User findUserProfileByJwt(String jwt) throws Exception {
        String email = JwtProvider.getEmailFromToken(jwt);
        User user  = userRepo.findByEmail(email);

        if(user==null) {
            throw new Exception("User not found By Jwt");
        }
        return user;
    }

    @Override
    public User findUserByEmail(String email) throws Exception {
        User user  = userRepo.findByEmail(email);

        if(user==null) {
            throw new Exception("User not found By Email");
        }
        return user;
    }

    @Override
    public User findUserById(Long userId) throws Exception {
        Optional<User> user = userRepo.findById(userId);
        if(user.isEmpty()){
            throw new Exception("User not found by UserId");
        }
        return user.get();
    }

    @Override
    public User enableTwoFactorAuthentication(Verification_Type verificationType,String sendTo,User user) {
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnabled(true);
        twoFactorAuth.setSendTo(verificationType);

        user.setTwoFactorAuth(twoFactorAuth);
        return userRepo.save(user);
    }

    @Override
    public User updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);

        return userRepo.save(user);
    }
}
