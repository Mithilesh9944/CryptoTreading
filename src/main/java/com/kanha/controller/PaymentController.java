package com.kanha.controller;

import com.kanha.service.IPaymentService;
import com.kanha.service.IUserService;
import com.kanha.domain.PaymentMethod;
import com.kanha.model.PaymentOrder;
import com.kanha.model.User;
import com.kanha.response.PaymentResponse;
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
@Tag(name = "Payment Management", description = "APIs for handling user payments and payment orders")
public class PaymentController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IPaymentService paymentService;

    @PostMapping("/payment/{paymentMethod}/amount/{amount}")
    @Operation(
            summary = "Initiate a payment",
            description = "Creates a payment order and generates a payment link (Razorpay/Stripe) for the logged-in user"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Payment order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid payment request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<PaymentResponse> paymentHandler(
            @PathVariable PaymentMethod paymentMethod,
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        PaymentResponse paymentResponse = null;
        PaymentOrder order = paymentService.createOrder(user, amount, paymentMethod);

        if (paymentMethod.equals(PaymentMethod.RAZORPAY)) {
            paymentResponse = paymentService.createRazorPaymentLink(user, amount, order.getId());
        } else if (paymentMethod.equals(PaymentMethod.STRIPE)) {
            paymentResponse = paymentService.createStripePaymentLink(user, amount, order.getId());
        }

        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }
}
