package com.kanha.repository;

import com.kanha.model.Watchlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WatchlistRepo extends JpaRepository<Watchlist,Long> {
    Watchlist findByUserId(Long userId);
}
