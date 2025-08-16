package com.kanha.service;

import com.kanha.domain.PaymentMethod;
import com.kanha.model.PaymentOrder;
import com.kanha.model.User;
import com.kanha.response.PaymentResponse;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;

public interface IPaymentService {
    PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod);
    PaymentOrder getPaymentOrderById(Long id) throws Exception;
    Boolean proceedPaymentOrder(PaymentOrder paymentOrder,String paymentId) throws RazorpayException;
    PaymentResponse createRazorPaymentLink(User user,Long amount,Long orderId) throws RazorpayException;
    PaymentResponse createStripePaymentLink(User user,Long amount,Long orderId) throws StripeException;

}
