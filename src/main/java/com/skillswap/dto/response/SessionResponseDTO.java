package com.skillswap.dto.response;

import com.skillswap.enums.SessionStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SessionResponseDTO {
    private Long id;
    private Long swapRequestId;
    private LocalDateTime scheduledAt;
    private SessionStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private Long senderUserId;
    private String senderUsername;
    private String receiverUsername;
}