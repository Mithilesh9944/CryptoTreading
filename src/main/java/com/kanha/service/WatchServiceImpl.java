package com.kanha.service;


import com.kanha.repository.WatchlistRepo;
import com.kanha.model.Coin;
import com.kanha.model.User;
import com.kanha.model.Watchlist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WatchServiceImpl implements IWatchlistService{
    @Autowired
    private WatchlistRepo watchlistRepo;
    @Override
    public Watchlist findUserWatchlist(Long userId) throws Exception {
        Watchlist watchlist = watchlistRepo.findByUserId(userId);
        if(watchlist==null){
            throw new Exception("Watchlist not found");
        }
        return watchlist;
    }

    @Override
    public Watchlist createWatchlist(User user) {
        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);
        return watchlistRepo.save(watchlist);
    }

    @Override
    public Watchlist findById(Long id) throws Exception {
        Optional<Watchlist> watchlist = watchlistRepo.findById(id);
        if (watchlist.isEmpty()){
            throw new Exception("Watchlist is not found By id");
        }
        return watchlist.get();
    }

    @Override
    public Coin addItemToWatchlist(Coin coin, User user) throws Exception {
        Watchlist watchlist = findUserWatchlist(user.getId());
        if(watchlist.getCoins().contains(coin)){
            watchlist.getCoins().remove(coin);
        }else {
            watchlist.getCoins().add(coin);
        }
        watchlistRepo.save(watchlist);
        return coin;
    }
}
