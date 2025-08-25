package com.kanha.controller;

import com.kanha.service.IPaymentDetailsService;
import com.kanha.service.IUserService;
import com.kanha.model.PaymentDetails;
import com.kanha.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "Payment Details Management", description = "APIs for managing user payment details")
public class PaymentDetailsController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IPaymentDetailsService paymentDetailsService;

    @PostMapping("/payment-details")
    @Operation(
            summary = "Add user payment details",
            description = "Save bank account details (account number, holder name, IFSC, bank name) for the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment details added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    })
    public ResponseEntity<PaymentDetails> addPaymentDetails(
            @RequestBody PaymentDetails paymentDetailsRequest,
            @RequestHeader("Authorization")String jwt
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        PaymentDetails paymentDetails = paymentDetailsService.addPaymentDetails(
                paymentDetailsRequest.getAccountNumber(),
                paymentDetailsRequest.getAccountHolderName(),
                paymentDetailsRequest.getIfsc(),
                paymentDetailsRequest.getBankName(),
                user
        );

        return new ResponseEntity<>(paymentDetails, HttpStatus.CREATED);
    }

    @GetMapping("/payment-details")
    @Operation(
            summary = "Get logged-in user's payment details",
            description = "Retrieve the saved bank account details of the authenticated user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Payment details retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT"),
            @ApiResponse(responseCode = "404", description = "Payment details not found")
    })
    public ResponseEntity<PaymentDetails> getUserPaymentDetails(
            @RequestHeader("Authorization")String jwt
    )throws Exception{
        User user = userService.findUserProfileByJwt(jwt);
        PaymentDetails paymentDetails = paymentDetailsService.getUsersPaymentDetails(user);
        return new ResponseEntity<>(paymentDetails,HttpStatus.OK);
    }
}
