package com.kanha.controller;

import com.kanha.service.IAssetService;
import com.kanha.service.IUserService;
import com.kanha.model.Asset;
import com.kanha.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asset")
@Tag(name = "Asset Controller", description = "Operations related to user assets in the CryptoTrade application")
@SecurityRequirement(name = "bearerAuth") // 🔐 requires JWT globally for all endpoints in this controller
public class AssetController {

    @Autowired
    private IAssetService assetService;

    @Autowired
    private IUserService userService;

    @Operation(summary = "Get Asset by ID", description = "Fetch a specific asset using its ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Asset not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    })
    @GetMapping("/{assetId}")
    public ResponseEntity<Asset> getAssetById(@PathVariable Long assetId) throws Exception {
        Asset asset = assetService.getAssetById(assetId);
        return ResponseEntity.ok().body(asset);
    }

    @Operation(summary = "Get Asset by User and Coin", description = "Fetch a specific asset by the logged-in user and a given coin ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Asset retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Asset not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    })
    @GetMapping("/coin/{coinId}/user")
    public ResponseEntity<Asset> getAssetByUserIdAndCoinId(
            @PathVariable String coinId,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        Asset asset = assetService.findAssetByUserIdAndCoinId(user.getId(), coinId);
        return new ResponseEntity<>(asset,HttpStatus.OK);
    }

    //Get all assets for logged-in user
    @Operation(summary = "Get All Assets for User", description = "Fetch all assets owned by the logged-in user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of assets retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT")
    })
    @GetMapping
    public ResponseEntity<List<Asset>> getAssetsForUser(
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user = userService.findUserProfileByJwt(jwt);
        List<Asset> assets = assetService.getUserAssets(user.getId());
        return ResponseEntity.ok().body(assets);
    }
}
