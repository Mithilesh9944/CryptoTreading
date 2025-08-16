package com.kanha.repository;

import com.kanha.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssetRepo extends JpaRepository<Asset,Long> {
    List<Asset> findByUserId(Long userId);
    Asset findByUserIdAndCoinId(Long userId,String coinId);
}
