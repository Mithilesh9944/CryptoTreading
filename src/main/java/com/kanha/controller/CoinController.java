package com.kanha.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kanha.service.ICoinService;
import com.kanha.model.Coin;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coins")
@Tag(name = "Coin Controller", description = "Endpoints for fetching cryptocurrency data")
public class CoinController {

    @Autowired
    private ICoinService coinService;
    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    @Operation(summary = "Get Coin List", description = "Fetch a paginated list of coins")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of coins retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid page parameter")
    })
    public ResponseEntity<List<Coin>> getCoinList(
            @RequestParam(required = false, name="page") int page
    ) throws Exception {
        List<Coin> coins = coinService.getCoinList(page);
        return new ResponseEntity<>(coins, HttpStatus.ACCEPTED);
    }

    @GetMapping("/{coinId}/chart")
    @Operation(summary = "Get Market Chart", description = "Retrieve market chart data for a specific coin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Market chart retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Coin not found")
    })
    public ResponseEntity<JsonNode> getMarketChart(
            @PathVariable String coinId,
            @RequestParam("days") int days
    ) throws Exception {
        String response = coinService.getMarketChart(coinId,days);
        JsonNode jsonNode = objectMapper.readTree(response);
        return new ResponseEntity<>(jsonNode,HttpStatus.ACCEPTED);
    }

    @GetMapping("/search")
    @Operation(summary = "Search Coin", description = "Search for coins by keyword")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Search results returned successfully")
    })
    public ResponseEntity<JsonNode> searchCoin(@RequestParam("q") String keyword) throws Exception {
        String response= coinService.searchCoin(keyword);
        JsonNode jsonNode = objectMapper.readTree(response);
        return ResponseEntity.ok(jsonNode);
    }

    @GetMapping("/top50")
    @Operation(summary = "Top 50 Coins", description = "Get the top 50 coins by market cap rank")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Top 50 coins retrieved successfully")
    })
    public ResponseEntity<JsonNode> getTop50CoinByMarketCapRank() throws Exception {
        String coins = coinService.getTop50CoinsByMarketCapRank();
        JsonNode jsonNode = objectMapper.readTree(coins);
        return ResponseEntity.ok(jsonNode);
    }

    @GetMapping("/trending")
    @Operation(summary = "Trending Coins", description = "Get trending cryptocurrency data")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Trending coins retrieved successfully")
    })
    public ResponseEntity<JsonNode> getTreadingCoin() throws Exception {
        String coins = coinService.getTreadingCoins();
        JsonNode jsonNode = objectMapper.readTree(coins);
        return ResponseEntity.ok(jsonNode);
    }

    @GetMapping("/details/{coinId}")
    @Operation(summary = "Coin Details", description = "Fetch detailed information about a specific coin")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Coin details retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Coin not found")
    })
    public ResponseEntity<JsonNode> getCoinDetails(@PathVariable String coinId) throws Exception {
        String coins = coinService.getCoinDetails(coinId);
        JsonNode jsonNode = objectMapper.readTree(coins);
        return ResponseEntity.ok(jsonNode);
    }
}
