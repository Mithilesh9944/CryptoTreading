package com.kanha.service;

import com.kanha.model.User;
import com.kanha.model.Withdrawal;

import java.util.List;

public interface IWithdrawalService {
    Withdrawal requestWithdrawal(Long amount, User user);
    Withdrawal proceedWithdrawal(Long withdrawalId,boolean accept) throws Exception;
    List<Withdrawal> getUserWithdrawalHistory(User user);
    List<Withdrawal> getAllWithdrawalRequest();
}
