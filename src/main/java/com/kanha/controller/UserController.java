package com.kanha.controller;

import com.kanha.request.ForgotPasswordTokenRequest;
import com.kanha.service.EmailService;
import com.kanha.service.IForgotPasswordService;
import com.kanha.service.IUserService;
import com.kanha.service.IVerificationCodeService;
import com.kanha.domain.Verification_Type;
import com.kanha.model.ForgotPasswordToken;
import com.kanha.model.User;
import com.kanha.model.VerificationCode;
import com.kanha.request.ResetPasswordRequest;
import com.kanha.response.ApiResponse;
import com.kanha.response.AuthResponse;
import com.kanha.utils.OTPUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private IVerificationCodeService verificationCodeService;
    @Autowired
    private IForgotPasswordService forgotPasswordService;

    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        return new ResponseEntity<User>(user, HttpStatus.OK);
    }
    @PostMapping("/api/users/verification/{verificationType}/send-to")
    public ResponseEntity<String> sendVerificationOtp(@RequestHeader("Authorization") String jwt ,
                                                    @PathVariable Verification_Type verificationType) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if(verificationCode==null){
            verificationCode=verificationCodeService.sendVerificationCode(user,verificationType);
        }

        if(verificationType.equals(Verification_Type.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(),verificationCode.getOtp());
        }
        return new ResponseEntity<>("Verification OTP has been sent successfully",HttpStatus.OK);
    }

    @PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthentication(@RequestHeader("Authorization")String jwt,
                                                              @PathVariable String otp) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());
        String sendTo = verificationCode.getVerificationType().equals(Verification_Type.EMAIL)
                                    ?verificationCode.getEmail()
                                    :verificationCode.getMobile();

        boolean isVerified = verificationCode.getOtp().equals(otp);

        if(isVerified){
            User updatedUser = userService.enableTwoFactorAuthentication(verificationCode.getVerificationType(),
                    sendTo,user);
            verificationCodeService.deleteVerificationCodeById(verificationCode);
            return new ResponseEntity<>(updatedUser,HttpStatus.OK);
        }
        throw new Exception("Wrong OTP");
    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(@RequestBody ForgotPasswordTokenRequest req) throws Exception {
        User user = userService.findUserByEmail(req.getSendTo());
        String otp = OTPUtils.generateOTP();
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findByUser(user.getId());
        if(forgotPasswordToken==null){
            forgotPasswordToken=forgotPasswordService.createToken(user,id,otp,req.getVerificationType(), req.getSendTo());
        }

        if(req.getVerificationType().equals(Verification_Type.EMAIL)){
            emailService.sendVerificationOtpEmail(user.getEmail(),forgotPasswordToken.getOtp());
        }
        AuthResponse response = new AuthResponse();
        response.setSession(forgotPasswordToken.getId());
        response.setMessage("Password Rest otp sent Successfully");
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(@RequestParam String id, @RequestBody ResetPasswordRequest req) throws Exception {
        ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findById(id);

        boolean isVerified = forgotPasswordToken.getOtp().equals(req.getOtp());
        if(isVerified){
            userService.updatePassword(forgotPasswordToken.getUser(),req.getPassword());
            ApiResponse res = new ApiResponse();
            res.setMessage("Password updated successfully");
            return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
        }
        throw new Exception("wrong otp");
    }

}
