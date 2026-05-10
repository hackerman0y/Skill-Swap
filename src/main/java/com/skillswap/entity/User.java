package com.skillswap.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(unique = true)
    private String email;

    @Column(nullable = false)
    private String role = "USER"; // "USER" or "ADMIN"

    // getter + setter
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;


    @Column(columnDefinition = "boolean default false")
    private Boolean online = false;

    @Column(columnDefinition = "integer default 0")
    private Integer trustScore = 0;

    @Column(name = "active_badge_id")
    private Long activeBadgeId;

    @Column(name = "active_badge_name")
    private String activeBadgeName;

    @Column(name = "active_badge_type")
    private String activeBadgeType;


// ── Getters & Setters ─────────────────────────────────────────

    public Long getActiveBadgeId() { return activeBadgeId; }
    public void setActiveBadgeId(Long activeBadgeId) { this.activeBadgeId = activeBadgeId; }

    public String getActiveBadgeName() { return activeBadgeName; }
    public void setActiveBadgeName(String activeBadgeName) { this.activeBadgeName = activeBadgeName; }

    public String getActiveBadgeType() { return activeBadgeType; }
    public void setActiveBadgeType(String activeBadgeType) { this.activeBadgeType = activeBadgeType; }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Skill> skills;


}