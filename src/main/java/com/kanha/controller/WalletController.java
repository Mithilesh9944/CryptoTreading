package com.kanha.controller;

import com.kanha.service.IOrderService;
import com.kanha.service.IPaymentService;
import com.kanha.service.IUserService;
import com.kanha.service.IWalletService;
import com.kanha.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
@Tag(name = "Wallet Controller", description = "Operations related to user wallet")
public class WalletController {

    @Autowired
    private IWalletService walletService;
    @Autowired
    private IUserService userService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private IPaymentService paymentService;

    @GetMapping
    @Operation(
            summary = "Get User Wallet",
            description = "Fetch the wallet details of the logged-in user using JWT",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "202", description = "Wallet retrieved successfully",
                            content = @Content(schema = @Schema(implementation = Wallet.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
            }
    )
    public ResponseEntity<Wallet> getUserWallet(
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);
        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/{walletId}/transfer")
    @Operation(
            summary = "Wallet to Wallet Transfer",
            description = "Transfer amount from the logged-in user's wallet to another wallet",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "202", description = "Transfer completed successfully",
                            content = @Content(schema = @Schema(implementation = Wallet.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Wallet not found")
            }
    )
    public ResponseEntity<Wallet> walletToWalletTransfer(
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
            @Parameter(description = "Receiver Wallet ID") @PathVariable Long walletId,
            @RequestBody WalletTransaction req
    ) throws Exception {
        User senderUser = userService.findUserProfileByJwt(jwt);
        Wallet receiverWallet = walletService.findWalletById(walletId);
        Wallet wallet = walletService.walletToWalletTransfer(senderUser, receiverWallet, req.getAmount());

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/order/{orderId}/pay")
    @Operation(
            summary = "Pay Order from Wallet",
            description = "Pay for an order using the logged-in user’s wallet",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "202", description = "Payment successful",
                            content = @Content(schema = @Schema(implementation = Wallet.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    public ResponseEntity<Wallet> payOrderPayment(
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
            @Parameter(description = "Order ID") @PathVariable Long orderId
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Order order = orderService.getOrderById(orderId);
        Wallet wallet = walletService.payOrderPayment(order, user);
        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/deposit")
    @Operation(
            summary = "Deposit Balance to Wallet",
            description = "Deposit balance into the user’s wallet after payment verification",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(responseCode = "202", description = "Balance added successfully",
                            content = @Content(schema = @Schema(implementation = Wallet.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "400", description = "Invalid payment")
            }
    )
    public ResponseEntity<Wallet> addBalanceToWallet(
            @Parameter(hidden = true) @RequestHeader("Authorization") String jwt,
            @Parameter(description = "Payment Order ID") @RequestParam(name = "order_id") Long orderId,
            @Parameter(description = "Payment ID") @RequestParam(name = "payment_id") String paymentId
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);

        Wallet wallet = walletService.getUserWallet(user);

        PaymentOrder order = paymentService.getPaymentOrderById(orderId);

        Boolean status = paymentService.proceedPaymentOrder(order, paymentId);
        if (wallet.getBalance() == null) {
            wallet.setBalance(BigDecimal.valueOf(0));
        }
        if (status) {
            wallet = walletService.addBalance(wallet, order.getAmount());
        }
        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }
}
