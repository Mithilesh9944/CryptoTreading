package com.kanha.service;

import com.kanha.repository.PaymentDetailsRepo;
import com.kanha.model.PaymentDetails;
import com.kanha.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentDetailsServiceImpl implements IPaymentDetailsService{
    @Autowired
    private PaymentDetailsRepo paymentDetailsRepo;
    @Override
    public PaymentDetails addPaymentDetails(String accNumber, String accHolderName, String ifsc, String bankName, User user) {
        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setUser(user);
        paymentDetails.setIfsc(ifsc);
        paymentDetails.setBankName(bankName);
        paymentDetails.setAccountNumber(accNumber);
        paymentDetails.setAccountHolderName(accHolderName);
        return paymentDetailsRepo.save(paymentDetails);
    }

    @Override
    public PaymentDetails getUsersPaymentDetails(User user) {
        return paymentDetailsRepo.findByUserId(user.getId());
    }
}
