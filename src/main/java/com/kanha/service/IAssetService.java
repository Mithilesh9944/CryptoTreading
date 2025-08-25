package com.kanha.service;

import com.kanha.model.Asset;
import com.kanha.model.Coin;
import com.kanha.model.User;

import java.util.List;

public interface IAssetService {
    Asset createAsset(User user, Coin coin,double quantity);
    Asset getAssetById(Long assetId) throws Exception;
    Asset getAssetByUserIdAndId(Long userId,Long assetId);
    List<Asset> getUserAssets(Long userId);
    Asset updateAsset(Long assetId,double quantity) throws Exception;
    Asset findAssetByUserIdAndCoinId(Long userId,String coinId);
    void deleteAsset(Long assetId);
}
