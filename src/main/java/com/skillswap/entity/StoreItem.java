package com.skillswap.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "store_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer price;

    private String category;

    private String icon;

    @Column(columnDefinition = "boolean default true")
    private Boolean available = true;
}