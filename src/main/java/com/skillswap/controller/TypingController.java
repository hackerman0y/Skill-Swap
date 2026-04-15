package com.skillswap.controller;

import org.springframework.messaging.handler.annotation.*;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class TypingController {

    @MessageMapping("/typing/{swapId}")
    @SendTo("/topic/typing/{swapId}")
    public Map<String, Object> handleTyping(
            @DestinationVariable Long swapId,
            @Payload Map<String, Object> payload) {
        return payload; // { userId, isTyping }
    }
}