package com.kanha.controller;

import com.kanha.service.IPaymentService;
import com.kanha.service.IUserService;
import com.kanha.domain.PaymentMethod;
import com.kanha.model.PaymentOrder;
import com.kanha.model.User;
import com.kanha.response.PaymentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IPaymentService paymentService;

    @PostMapping("/payment/{paymentMethod}/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler(
            @PathVariable PaymentMethod paymentMethod,
            @PathVariable Long amount,
            @RequestHeader("Authorization")String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        PaymentResponse paymentResponse = null;
        PaymentOrder order = paymentService.createOrder(user,amount,paymentMethod);

        if(paymentMethod.equals(PaymentMethod.RAZORPAY)){
            paymentResponse = paymentService.createRazorPaymentLink(user,amount, order.getId());
        }else if(paymentMethod.equals(PaymentMethod.STRIPE)){
            paymentResponse = paymentService.createStripePaymentLink(user,amount, order.getId());
        }
        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }
}
