package com.skillswap.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private StoreItem item;

    @Column(nullable = false)
    private LocalDateTime purchasedAt;

    @PrePersist
    public void prePersist() {
        if (purchasedAt == null) purchasedAt = LocalDateTime.now();
    }
}