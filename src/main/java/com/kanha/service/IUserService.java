package com.kanha.service;

import com.kanha.domain.Verification_Type;
import com.kanha.model.User;

public interface IUserService {
    public User findUserProfileByJwt(String jwt) throws Exception;
    public User findUserByEmail(String email) throws Exception;
    public User findUserById(Long userId) throws Exception;
    public User enableTwoFactorAuthentication(Verification_Type verificationType,String sendTo,User user);
    public User updatePassword(User user,String newPassword);
}
