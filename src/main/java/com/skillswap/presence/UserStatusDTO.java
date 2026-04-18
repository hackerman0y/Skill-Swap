package com.skillswap.presence;

public class UserStatusDTO {

    private Long userId;
    private String status; // online / offline

    public UserStatusDTO(Long userId, String status) {
        this.userId = userId;
        this.status = status;
    }

    public Long getUserId() {
        return userId;
    }

    public String getStatus() {
        return status;
    }
}