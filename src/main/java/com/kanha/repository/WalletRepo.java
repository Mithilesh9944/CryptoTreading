package com.kanha.repository;

import com.kanha.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepo extends JpaRepository<Wallet,Long> {
    Wallet findByUserId(Long userId);
}
