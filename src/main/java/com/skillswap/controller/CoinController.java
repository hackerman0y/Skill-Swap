package com.skillswap.controller;

import com.skillswap.entity.CoinTransaction;
import com.skillswap.entity.StoreItem;
import com.skillswap.entity.User;
import com.skillswap.entity.UserInventory;
import com.skillswap.service.CoinService;
import com.skillswap.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
public class CoinController {

    private final CoinService coinService;
    private final UserService userService;

    public CoinController(CoinService coinService, UserService userService) {
        this.coinService = coinService;
        this.userService = userService;
    }

    @GetMapping("/api/coins/balance")
    public ResponseEntity<Map<String, Integer>> getBalance(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        int balance = coinService.getBalance(user.getId());
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    @GetMapping("/api/coins/transactions")
    public ResponseEntity<List<CoinTransaction>> getTransactions(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(coinService.getTransactionHistory(user.getId()));
    }

    @PostMapping("/api/coins/earn")
    public ResponseEntity<CoinTransaction> earnCoins(
            Principal principal,
            @RequestBody Map<String, Object> body) {

        User user = userService.getUserByEmail(principal.getName());
        int amount = (int) body.get("amount");
        String reason = (String) body.getOrDefault("reason", "Session completed");

        CoinTransaction transaction = coinService.earnCoins(user.getId(), amount, reason);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping("/api/store/items")
    public ResponseEntity<List<StoreItem>> getStoreItems() {
        return ResponseEntity.ok(coinService.getStoreItems());
    }

    @PostMapping("/api/store/buy/{itemId}")
    public ResponseEntity<?> buyItem(
            Principal principal,
            @PathVariable Long itemId) {
        try {
            User user = userService.getUserByEmail(principal.getName());
            UserInventory inventory = coinService.buyItem(user.getId(), itemId);
            return ResponseEntity.ok(inventory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/api/store/inventory")
    public ResponseEntity<List<UserInventory>> getInventory(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(coinService.getUserInventory(user.getId()));
    }
}