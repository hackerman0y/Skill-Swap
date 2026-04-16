package com.skillswap.dto.response;

import com.skillswap.enums.SwapStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SwapResponseDTO {
    private Long id;
    private Long senderId;
    private String senderUsername;
    private Long receiverId;
    private String receiverUsername;
    private Long offeredSkillId;
    private String offeredSkillName;
    private Long wantedSkillId;
    private String wantedSkillName;
    private SwapStatus status;
    private LocalDateTime createdAt;
}