package com.skillswap.repository;

import com.skillswap.entity.UserInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserInventoryRepository extends JpaRepository<UserInventory, Long> {

    List<UserInventory> findByUserId(Long userId);

    boolean existsByUserIdAndItemId(Long userId, Long itemId);
}