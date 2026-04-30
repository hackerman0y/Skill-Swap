package com.skillswap.repository;

import com.skillswap.entity.StoreItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StoreItemRepository extends JpaRepository<StoreItem, Long> {

    List<StoreItem> findByAvailableTrue();
}