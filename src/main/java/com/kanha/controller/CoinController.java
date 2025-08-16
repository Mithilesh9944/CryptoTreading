package com.kanha.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanha.service.ICoinService;
import com.kanha.model.Coin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coins")
public class CoinController {
    @Autowired
    private ICoinService coinService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<Coin>> getCoinList(@RequestParam(required = false,name="page")int page) throws Exception {
        List<Coin> coins = coinService.getCoinList(page);
        return new ResponseEntity<>(coins, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{coinId}/chart")
    public ResponseEntity<JsonNode> getMarketChart(@PathVariable String coinId, @RequestParam("days") int days) throws Exception {
        String response = coinService.getMarketChart(coinId,days);
        JsonNode jsonNode = objectMapper.readTree(response);
        return new ResponseEntity<>(jsonNode,HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    public ResponseEntity<JsonNode> searchCoin(@RequestParam("q") String keyword) throws Exception {
        String response= coinService.searchCoin(keyword);
        JsonNode jsonNode = objectMapper.readTree(response);
        return ResponseEntity.ok(jsonNode);
    }

    @GetMapping("/top50")
    public ResponseEntity<JsonNode> getTop50CoinByMarketCapRank() throws Exception {
        String coins = coinService.getTop50CoinsByMarketCapRank();
        JsonNode jsonNode = objectMapper.readTree(coins);
        return ResponseEntity.ok(jsonNode);
    }

    @GetMapping("/trending")
    public ResponseEntity<JsonNode> getTreadingCoin() throws Exception {
        String coins = coinService.getTreadingCoins();
        JsonNode jsonNode = objectMapper.readTree(coins);
        return ResponseEntity.ok(jsonNode);
    }

    @GetMapping("/details/{coinId}")
    public ResponseEntity<JsonNode> getCoinDetails(@PathVariable String coinId) throws Exception {
        String coins = coinService.getCoinDetails(coinId);
        JsonNode jsonNode = objectMapper.readTree(coins);
        return ResponseEntity.ok(jsonNode);
    }
}
