package com.skillswap.dto.request;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SessionRequestDTO {
    private Long swapRequestId;
    private LocalDateTime scheduledAt;
}