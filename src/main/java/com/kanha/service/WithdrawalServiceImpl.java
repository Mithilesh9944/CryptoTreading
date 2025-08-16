package com.kanha.service;

import com.kanha.repository.WithdrawalRepo;
import com.kanha.domain.WithdrawalStatus;
import com.kanha.model.User;
import com.kanha.model.Withdrawal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WithdrawalServiceImpl implements IWithdrawalService {
    @Autowired
    private WithdrawalRepo withdrawalRepo;
    @Override
    public Withdrawal requestWithdrawal(Long amount, User user) {
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setAmount(amount);
        withdrawal.setUser(user);
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        return withdrawalRepo.save(withdrawal);
    }

    @Override
    public Withdrawal proceedWithdrawal(Long withdrawalId, boolean accept) throws Exception {
        Optional<Withdrawal> withdrawal = withdrawalRepo.findById(withdrawalId);
        if(withdrawal.isEmpty()){
            throw new Exception("withdrawal not found");
        }
        Withdrawal withdrawal1 = withdrawal.get();
        withdrawal1.setDate(LocalDateTime.now());
        if(accept){
            withdrawal1.setStatus(WithdrawalStatus.SUCCESS);
        }else {
            withdrawal1.setStatus(WithdrawalStatus.DECLINE);
        }
        return withdrawalRepo.save(withdrawal1);
    }

    @Override
    public List<Withdrawal> getUserWithdrawalHistory(User user) {
        return withdrawalRepo.findByUserId(user.getId());
    }

    @Override
    public List<Withdrawal> getAllWithdrawalRequest() {
        return withdrawalRepo.findAll();
    }
}
