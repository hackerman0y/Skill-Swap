package com.skillswap.controller;

import com.skillswap.entity.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat")
    public void SendMessage(@Payload ChatMessage message ){
        messagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/messages",
                message
        );
    }

    @MessageMapping("/chat.broadcast")
    public void BroadcastMessage(@Payload ChatMessage message){
        messagingTemplate.convertAndSend("/topic/public", message);
    }
}
