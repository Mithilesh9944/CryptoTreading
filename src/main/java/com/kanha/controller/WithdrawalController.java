package com.kanha.controller;

import com.kanha.service.IUserService;
import com.kanha.service.IWalletService;
import com.kanha.service.IWithdrawalService;
import com.kanha.model.User;
import com.kanha.model.Wallet;
import com.kanha.model.Withdrawal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WithdrawalController {
    @Autowired
    private IWithdrawalService withdrawalService;
    @Autowired
    private IWalletService walletService;
    @Autowired
    private IUserService userService;

    @PostMapping("/api/withdrawal/{amount}")
    public ResponseEntity<Withdrawal> withdrawalRequest(
        @RequestHeader("Authorization")String jwt,
        @PathVariable Long amount
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Wallet userWallet = walletService.getUserWallet(user);
        Withdrawal withdrawal = withdrawalService.requestWithdrawal(amount,user);

        walletService.addBalance(userWallet,-withdrawal.getAmount());
       // WalletTransaction walletTransaction = walletTransactionService.createTransaction
        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @PatchMapping("/api/admin/withdrawal/{id}/proceed/{accept}")
    public ResponseEntity<Withdrawal> proceedWithdrawal(
            @PathVariable Long id,
            @PathVariable boolean accept,
            @RequestHeader("Authorization")String jwt
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Withdrawal withdrawal= withdrawalService.proceedWithdrawal(id,accept);

        Wallet userWallet = walletService.getUserWallet(user);
        if(!accept){
            walletService.addBalance(userWallet,withdrawal.getAmount());
        }

        return new ResponseEntity<>(withdrawal,HttpStatus.OK);
    }
    @GetMapping("/api/withdrawal")
    public ResponseEntity<List<Withdrawal>> getWithdrawalHistory(@RequestHeader("Authorization")String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<Withdrawal> withdrawals = withdrawalService.getUserWithdrawalHistory(user);
        return new ResponseEntity<>(withdrawals,HttpStatus.OK);
    }

    @GetMapping("/api/admin/withdrawal")
    public ResponseEntity<List<Withdrawal>> getAllWithdrawalRequest(@RequestHeader("Authorization")String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<Withdrawal> withdrawals = withdrawalService.getAllWithdrawalRequest();
        return new ResponseEntity<>(withdrawals,HttpStatus.OK);
    }
}
