package com.skillswap.service;

import com.skillswap.entity.CoinTransaction;
import com.skillswap.entity.StoreItem;
import com.skillswap.entity.User;
import com.skillswap.entity.UserInventory;
import com.skillswap.repository.CoinTransactionRepository;
import com.skillswap.repository.StoreItemRepository;
import com.skillswap.repository.UserInventoryRepository;
import com.skillswap.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CoinService {

    private final UserRepository userRepository;
    private final CoinTransactionRepository coinTransactionRepository;
    private final StoreItemRepository storeItemRepository;
    private final UserInventoryRepository userInventoryRepository;

    public CoinService(UserRepository userRepository,
                       CoinTransactionRepository coinTransactionRepository,
                       StoreItemRepository storeItemRepository,
                       UserInventoryRepository userInventoryRepository) {
        this.userRepository = userRepository;
        this.coinTransactionRepository = coinTransactionRepository;
        this.storeItemRepository = storeItemRepository;
        this.userInventoryRepository = userInventoryRepository;
    }


    public int getBalance(Long userId) {
        List<CoinTransaction> transactions =
                coinTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId);

        int balance = 0;
        for (CoinTransaction t : transactions) {
            if ("earn".equals(t.getType())) {
                balance += t.getAmount();
            } else if ("spend".equals(t.getType())) {
                balance -= t.getAmount();
            }
        }
        return balance;
    }

    public CoinTransaction earnCoins(Long userId, int amount, String reason) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CoinTransaction transaction = CoinTransaction.builder()
                .user(user)
                .type("earn")
                .amount(amount)
                .reason(reason)
                .build();

        return coinTransactionRepository.save(transaction);
    }

    public List<CoinTransaction> getTransactionHistory(Long userId) {
        return coinTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<StoreItem> getStoreItems() {
        return storeItemRepository.findByAvailableTrue();
    }

    public UserInventory buyItem(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        StoreItem item = storeItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (userInventoryRepository.existsByUserIdAndItemId(userId, itemId)) {
            throw new RuntimeException("You already own this item");
        }

        int balance = getBalance(userId);
        if (balance < item.getPrice()) {
            throw new RuntimeException("Not enough coins");
        }

        CoinTransaction transaction = CoinTransaction.builder()
                .user(user)
                .type("spend")
                .amount(item.getPrice())
                .reason("Bought item: " + item.getName())
                .build();
        coinTransactionRepository.save(transaction);

        UserInventory inventory = UserInventory.builder()
                .user(user)
                .item(item)
                .build();

        return userInventoryRepository.save(inventory);
    }

    public List<UserInventory> getUserInventory(Long userId) {
        return userInventoryRepository.findByUserId(userId);
    }
}