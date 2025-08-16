package com.kanha.repository;

import com.kanha.model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepo extends JpaRepository<Coin,String> {

}
