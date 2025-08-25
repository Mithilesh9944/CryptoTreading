package com.kanha.service;

import com.kanha.model.Coin;

import java.util.List;

public interface ICoinService {
    List<Coin> getCoinList(int page) throws Exception;
    String getMarketChart(String coinId,int days) throws Exception;
    String getCoinDetails(String coinId) throws Exception;
    //This will return the coins which is present in out DB
    Coin findById(String coinId) throws Exception;
    String searchCoin(String keyword) throws Exception;
    String getTop50CoinsByMarketCapRank() throws Exception;
    String getTreadingCoins() throws Exception;
}
