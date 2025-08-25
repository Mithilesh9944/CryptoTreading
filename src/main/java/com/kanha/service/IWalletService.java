package com.kanha.service;

import com.kanha.model.Order;
import com.kanha.model.User;
import com.kanha.model.Wallet;

public interface IWalletService {
    Wallet getUserWallet(User user);
    Wallet addBalance(Wallet wallet,Long money);
    Wallet findWalletById(Long id) throws Exception;
    Wallet walletToWalletTransfer(User sender, Wallet receiverWallet,Long amount) throws Exception;
    Wallet payOrderPayment(Order order,User user) throws Exception;


}
