package com.kanha.service;

import com.kanha.model.PaymentDetails;
import com.kanha.model.User;

public interface IPaymentDetailsService {
    PaymentDetails addPaymentDetails(String accNumber,
                                     String accHolderName,
                                     String ifsc,
                                     String bankName,
                                     User user
                                     );
    PaymentDetails getUsersPaymentDetails(User user);
}
