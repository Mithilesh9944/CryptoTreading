package com.kanha.controller;

import com.kanha.service.ICoinService;
import com.kanha.service.IUserService;
import com.kanha.service.IWatchlistService;
import com.kanha.model.Coin;
import com.kanha.model.User;
import com.kanha.model.Watchlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchlistController {
    @Autowired
    private IUserService userService;
    @Autowired
    private IWatchlistService watchlistService;
    @Autowired
    private ICoinService coinService;

    @GetMapping("/user")
    public ResponseEntity<Watchlist> getUserWatchlist(@RequestHeader("Authorization")String jwt) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Watchlist watchlist = watchlistService.findUserWatchlist(user.getId());
        return ResponseEntity.ok(watchlist);
    }

    @GetMapping("/{watchlistId}")
    public ResponseEntity<Watchlist> getWatchlistById(@PathVariable Long watchlistId) throws Exception {
        Watchlist watchlist = watchlistService.findById(watchlistId);
        return ResponseEntity.ok(watchlist);
    }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<Coin> addItemToWatchlist(@RequestHeader("Authorization")String jwt,@PathVariable String coinId) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Coin coin = coinService.findById(coinId);
        Coin addedCoin = watchlistService.addItemToWatchlist(coin,user);
        return ResponseEntity.ok(addedCoin);
    }
}
