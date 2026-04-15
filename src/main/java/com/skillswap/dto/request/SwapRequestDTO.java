package com.skillswap.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class SwapRequestDTO {
    private Long receiverId;
    private Long offeredSkillId;
    private Long wantedSkillId;
}
