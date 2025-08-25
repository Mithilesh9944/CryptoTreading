package com.kanha.controller;

import com.kanha.service.ICoinService;
import com.kanha.service.IUserService;
import com.kanha.service.IWatchlistService;
import com.kanha.model.Coin;
import com.kanha.model.User;
import com.kanha.model.Watchlist;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
@Tag(name = "Watchlist Management", description = "APIs for managing user watchlists and items")
public class WatchlistController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IWatchlistService watchlistService;
    @Autowired
    private ICoinService coinService;

    @GetMapping("/user")
    @Operation(
            summary = "Get logged-in user's watchlist",
            description = "Fetch the watchlist of the authenticated user using JWT"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Watchlist retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT"),
            @ApiResponse(responseCode = "404", description = "Watchlist not found")
    })
    public ResponseEntity<Watchlist> getUserWatchlist(@RequestHeader("Authorization")String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Watchlist watchlist = watchlistService.findUserWatchlist(user.getId());
        return ResponseEntity.ok(watchlist);
    }

    @GetMapping("/{watchlistId}")
    @Operation(
            summary = "Get watchlist by ID",
            description = "Fetch a watchlist using its unique ID"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Watchlist retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Watchlist not found")
    })
    public ResponseEntity<Watchlist> getWatchlistById(@PathVariable Long watchlistId) throws Exception {
        Watchlist watchlist = watchlistService.findById(watchlistId);
        return ResponseEntity.ok(watchlist);
    }

    @PatchMapping("/add/coin/{coinId}")
    @Operation(
            summary = "Add coin to watchlist",
            description = "Add a coin to the authenticated user's watchlist"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coin added successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT"),
            @ApiResponse(responseCode = "404", description = "Coin or Watchlist not found")
    })
    public ResponseEntity<Coin> addItemToWatchlist(@RequestHeader("Authorization")String jwt,@PathVariable String coinId) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Coin coin = coinService.findById(coinId);
        Coin addedCoin = watchlistService.addItemToWatchlist(coin,user);
        return ResponseEntity.ok(addedCoin);
    }
}
