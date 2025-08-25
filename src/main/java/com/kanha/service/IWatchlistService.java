package com.kanha.service;

import com.kanha.model.Coin;
import com.kanha.model.User;
import com.kanha.model.Watchlist;

public interface IWatchlistService {
    Watchlist findUserWatchlist(Long userId) throws Exception;
    Watchlist createWatchlist(User user);
    Watchlist findById(Long id) throws Exception;
    Coin addItemToWatchlist(Coin coin,User user) throws Exception;
}
