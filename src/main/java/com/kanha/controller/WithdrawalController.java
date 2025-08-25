package com.kanha.controller;

import com.kanha.service.IUserService;
import com.kanha.service.IWalletService;
import com.kanha.service.IWithdrawalService;
import com.kanha.model.User;
import com.kanha.model.Wallet;
import com.kanha.model.Withdrawal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Withdrawal Controller", description = "APIs for handling withdrawal requests and approvals")
public class WithdrawalController {

    @Autowired
    private IWithdrawalService withdrawalService;
    @Autowired
    private IWalletService walletService;
    @Autowired
    private IUserService userService;

    @PostMapping("/api/withdrawal/{amount}")
    @Operation(summary = "Request a Withdrawal", description = "User requests to withdraw a specific amount")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Withdrawal request created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    })
    public ResponseEntity<Withdrawal> withdrawalRequest(
            @RequestHeader("Authorization") String jwt,
            @PathVariable Long amount
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Wallet userWallet = walletService.getUserWallet(user);
        Withdrawal withdrawal = withdrawalService.requestWithdrawal(amount, user);

        walletService.addBalance(userWallet, -withdrawal.getAmount());
        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @PatchMapping("/api/admin/withdrawal/{id}/proceed/{accept}")
    @Operation(summary = "Proceed Withdrawal (Admin)", description = "Admin accepts or rejects a withdrawal request")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Withdrawal processed successfully"),
            @ApiResponse(responseCode = "404", description = "Withdrawal not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    })
    public ResponseEntity<Withdrawal> proceedWithdrawal(
            @PathVariable Long id,
            @PathVariable boolean accept,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Withdrawal withdrawal = withdrawalService.proceedWithdrawal(id, accept);

        Wallet userWallet = walletService.getUserWallet(user);
        if (!accept) {
            walletService.addBalance(userWallet, withdrawal.getAmount());
        }

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @GetMapping("/api/withdrawal")
    @Operation(summary = "Get Withdrawal History", description = "Fetch withdrawal history for the logged-in user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Withdrawal history retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    })
    public ResponseEntity<List<Withdrawal>> getWithdrawalHistory(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<Withdrawal> withdrawals = withdrawalService.getUserWithdrawalHistory(user);
        return new ResponseEntity<>(withdrawals, HttpStatus.OK);
    }

    @GetMapping("/api/admin/withdrawal")
    @Operation(summary = "Get All Withdrawal Requests (Admin)", description = "Admin fetches all withdrawal requests")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "All withdrawal requests retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    })
    public ResponseEntity<List<Withdrawal>> getAllWithdrawalRequest(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<Withdrawal> withdrawals = withdrawalService.getAllWithdrawalRequest();
        return new ResponseEntity<>(withdrawals, HttpStatus.OK);
    }
}
