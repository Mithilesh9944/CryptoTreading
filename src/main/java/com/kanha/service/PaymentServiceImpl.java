package com.kanha.service;

import com.kanha.repository.PaymentOrderRepo;
import com.kanha.domain.PaymentMethod;
import com.kanha.domain.PaymentOrderStatus;
import com.kanha.model.PaymentOrder;
import com.kanha.model.User;
import com.kanha.response.PaymentResponse;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements IPaymentService{
    @Autowired
    private PaymentOrderRepo paymentOrderRepo;
    @Value("${stripe.api.key")
    private String stripeSecretKey;
    @Value("${razorpay.api.key}")
    private String apiKey;
    @Value("${razorpay.api.secret}")
    private String apiSecretKey;
    @Override
    public PaymentOrder createOrder(User user, Long amount, PaymentMethod paymentMethod) {
        PaymentOrder paymentOrder= new PaymentOrder();
        paymentOrder.setUser(user);
        paymentOrder.setAmount(amount);
        paymentOrder.setPaymentMethod(paymentMethod);
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        return paymentOrderRepo.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) throws Exception {
        return paymentOrderRepo.findById(id).orElseThrow(()->new Exception("Payment Order Not found"));
    }

    @Override
    public Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws RazorpayException {
        if(paymentOrder.getStatus()==null){
            paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        }
        if(paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)){
            if(paymentOrder.getPaymentMethod().equals(PaymentMethod.RAZORPAY)){
                RazorpayClient razorpay =new RazorpayClient(apiKey,apiSecretKey);
                Payment payment = razorpay.payments.fetch(paymentId);
                Integer amount = payment.get("amount");
                String status = payment.get("status");

                if(status.equals("captured")){
                    paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                    return true;
                }
                paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                paymentOrderRepo.save(paymentOrder);
                return false;
            }
            //Not Handled Case of STRIPE_PAY
            paymentOrder.setStatus(PaymentOrderStatus.FAILED);
            paymentOrderRepo.save(paymentOrder);
            return true;
        }
        return false;
    }

    @Override
    public PaymentResponse createRazorPaymentLink(User user, Long amount,Long orderId) throws RazorpayException {
        Long Amount = amount*100;
        try{
            //Instantiate a RazorPay client with your key ID and Secret
            RazorpayClient razorpay = new RazorpayClient(apiKey,apiSecretKey);
            //Create a JSON object with the payment link request
            JSONObject paymentLinkRequest = new JSONObject();
            paymentLinkRequest.put("amount",amount);
            paymentLinkRequest.put("currency","INR");

            //Create a JSON object with the customer details
            JSONObject customer = new JSONObject();
            customer.put("name",user.getFullName());
            customer.put("email",user.getEmail());
            paymentLinkRequest.put("customer",customer);

            //Create a JSON Object with the notification settings
            JSONObject notify = new JSONObject();
            notify.put("email",true);
            paymentLinkRequest.put("notify",notify);

            //Set the reminder Settings
            paymentLinkRequest.put("reminder_enable",true);

            //Set the callBack URL and Method
            paymentLinkRequest.put("callback_url","http://localhost:5173/wallet?order_id="+orderId);
            paymentLinkRequest.put("callback_method","get");

            //Create the payment link using the paymentLink.create() method
            PaymentLink payment = razorpay.paymentLink.create(paymentLinkRequest);

            String paymentLinkId = payment.get("id");
            String paymentLinkUrl = payment.get("short_url");

            PaymentResponse res = new PaymentResponse();
            res.setPayment_url(paymentLinkUrl);

            return res;

        } catch (RazorpayException e) {
            System.out.println("Error creating payment link: "+e.getMessage());
            throw new RazorpayException(e.getMessage());
        }
    }

    @Override
    public PaymentResponse createStripePaymentLink(User user, Long amount, Long orderId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/wallet?order_id="+orderId)
                .setCancelUrl("http://localhost:5173/payment/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(amount*100)
                                .setProductData(SessionCreateParams
                                        .LineItem
                                        .PriceData
                                        .ProductData
                                        .builder()
                                        .setName("Top Up Wallet")
                                        .build())
                                .build())
                        .build())
                .build();

        Session session = Session.create(params);

        System.out.println("session..........."+session);

        PaymentResponse res= new PaymentResponse();
        res.setPayment_url(session.getUrl());

        return res;
    }
}
