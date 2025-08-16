package com.kanha.controller;

import com.kanha.repository.UserRepo;
import com.kanha.service.*;
import com.kanha.config.JwtProvider;
import com.kanha.model.TwoFactorOTP;
import com.kanha.model.User;
import com.kanha.response.AuthResponse;
import com.kanha.utils.OTPUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private IUserService userService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private ITwoFactorOTPService twoFactorOTPService;
    @Autowired
    private CustomerUserDetailService userDetailService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private IWatchlistService watchlistService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {
        User isEmailExist = userRepo.findByEmail(user.getEmail());
        if(isEmailExist!=null){
            throw new Exception("User or Email Already Exist");
        }
        User newUser= new User();
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        User savedUser =userRepo.save(newUser);
        watchlistService.createWatchlist(savedUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = JwtProvider.generateToken(auth);
        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setStatus(true);
        response.setMessage("register success");
        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping("/signIn")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws MessagingException {
        String userName = user.getEmail();//Working With Email,So Took email as username
        String password = user.getPassword();
        Authentication authentication = authenticate(userName,password);

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(),user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = JwtProvider.generateToken(auth);
        User authUser = userRepo.findByEmail(userName);
        //If TwoFactorOTP is enabled
        if(user.getTwoFactorAuth().isEnabled()){
            AuthResponse otpResponse = new AuthResponse();
            otpResponse.setMessage("Two factor auth is enabled");
            otpResponse.setTwoFactorAuthEnabled(true);
            String otp = OTPUtils.generateOTP();

            TwoFactorOTP oldTwoFactorOTP = twoFactorOTPService.findByUser(authUser.getId());
            if(oldTwoFactorOTP!=null){
                twoFactorOTPService.deleteTwoFactorOtp(oldTwoFactorOTP);
            }
            TwoFactorOTP newTwoFactorOTP = twoFactorOTPService.createTwoFactorOtp(authUser,otp,jwt);
            emailService.sendVerificationOtpEmail(userName,otp);
            otpResponse.setSession(newTwoFactorOTP.getId());
            return new ResponseEntity<>(otpResponse,HttpStatus.ACCEPTED);
        }

        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setStatus(true);
        response.setMessage("login success");

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }
    private Authentication authenticate(String username,String password){
        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        if(userDetails==null){
            throw new BadCredentialsException("invalid username");
        }

        if(!password.equals(userDetails.getPassword())){
            throw new BadCredentialsException("invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails,password,userDetails.getAuthorities());
    }
    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySigingOTP(@PathVariable String otp,@RequestParam String id) throws Exception {

        TwoFactorOTP twoFactorOTP = twoFactorOTPService.findById(id);
        if(twoFactorOTPService.verifyTwoFactorOtp(twoFactorOTP,otp)){
            AuthResponse res  = new AuthResponse();
            res.setMessage("Two Factor Authentication verified");
            res.setTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOTP.getJwt());
            return new ResponseEntity<>(res,HttpStatus.OK);
        }
        throw new Exception("invalid otp");
    }
}
