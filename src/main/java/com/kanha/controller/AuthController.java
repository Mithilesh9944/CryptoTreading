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

// Swagger imports
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication Controller", description = "APIs for user authentication and authorization")
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

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account and returns a JWT token on successful signup.",
            requestBody = @RequestBody(
                    required = true,
                    description = "User registration details",
                    content = @Content(schema = @Schema(implementation = User.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Email already exists")
            }
    )
    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@org.springframework.web.bind.annotation.RequestBody User user) throws Exception {
        User isEmailExist = userRepo.findByEmail(user.getEmail());
        if (isEmailExist != null) {
            throw new Exception("User or Email Already Exist");
        }
        User newUser = new User();
        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        User savedUser = userRepo.save(newUser);
        watchlistService.createWatchlist(savedUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = JwtProvider.generateToken(auth);

        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setStatus(true);
        response.setMessage("register success");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Login with email & password",
            description = "Authenticates the user and returns a JWT token. If 2FA is enabled, sends OTP to email/mobile.",
            requestBody = @RequestBody(
                    required = true,
                    description = "Login request containing email and password",
                    content = @Content(schema = @Schema(implementation = com.kanha.request.SignInRequest.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "202", description = "Login successful or 2FA required"),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")
            }
    )
    @PostMapping("/signIn")
    public ResponseEntity<AuthResponse> login(@org.springframework.web.bind.annotation.RequestBody User user) throws MessagingException {
        String userName = user.getEmail();
        String password = user.getPassword();
        Authentication authentication = authenticate(userName, password);

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword());
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = JwtProvider.generateToken(auth);

        User authUser = userRepo.findByEmail(userName);

        // If TwoFactorOTP is enabled
        if (user.getTwoFactorAuth().isEnabled()) {
            AuthResponse otpResponse = new AuthResponse();
            otpResponse.setMessage("Two factor auth is enabled");
            otpResponse.setTwoFactorAuthEnabled(true);

            String otp = OTPUtils.generateOTP();
            TwoFactorOTP oldTwoFactorOTP = twoFactorOTPService.findByUser(authUser.getId());
            if (oldTwoFactorOTP != null) {
                twoFactorOTPService.deleteTwoFactorOtp(oldTwoFactorOTP);
            }
            TwoFactorOTP newTwoFactorOTP = twoFactorOTPService.createTwoFactorOtp(authUser, otp, jwt);
            emailService.sendVerificationOtpEmail(userName, otp);
            otpResponse.setSession(newTwoFactorOTP.getId());

            return new ResponseEntity<>(otpResponse, HttpStatus.ACCEPTED);
        }

        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setStatus(true);
        response.setMessage("login success");

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = userDetailService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new BadCredentialsException("invalid username");
        }
        if (!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Operation(
            summary = "Verify Two-Factor OTP",
            description = "Validates the OTP sent to the user during login if 2FA is enabled.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "OTP verified successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid OTP or session expired")
            }
    )
    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifySigingOTP(
            @PathVariable String otp,
            @RequestParam String id) throws Exception {

        TwoFactorOTP twoFactorOTP = twoFactorOTPService.findById(id);

        if (twoFactorOTPService.verifyTwoFactorOtp(twoFactorOTP, otp)) {
            AuthResponse res = new AuthResponse();
            res.setMessage("Two Factor Authentication verified");
            res.setTwoFactorAuthEnabled(true);
            res.setJwt(twoFactorOTP.getJwt());
            return new ResponseEntity<>(res, HttpStatus.OK);
        }
        throw new Exception("invalid otp");
    }
}
