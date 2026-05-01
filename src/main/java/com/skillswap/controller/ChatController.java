package com.skillswap.controller;

import com.skillswap.config.JwtUtil;
import com.skillswap.entity.ChatMessage;
import com.skillswap.repository.ChatMessageRepository;
import com.skillswap.repository.UserRepository;
import com.skillswap.entity.User;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public ChatController(SimpMessagingTemplate messagingTemplate,
                          ChatMessageRepository chatMessageRepository,
                          UserRepository userRepository,
                          JwtUtil jwtUtil) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }


    @MessageMapping("/chat")
    public void sendMessage(@Payload ChatMessage message) {
        if (message.getTimestamp() == null) {
            message.setTimestamp(LocalDateTime.now());
        }
        // Sync content/text fields (frontend sends both)
        if (message.getContent() == null && message.getText() != null) {
            message.setContent(message.getText());
        }

        // Persist to DB
        chatMessageRepository.save(message);

        // Route by receiver's username
        messagingTemplate.convertAndSendToUser(
                message.getReceiver(),
                "/queue/messages",
                message
        );
    }

    // ── FIX 2: Typing indicator — resolve username from receiverId ──
    @MessageMapping("/chat.typing")
    public void handleTyping(@Payload Map<String, Object> payload) {
        try {
            String receiverIdStr = payload.get("receiverId").toString();
            Long receiverId = Long.parseLong(receiverIdStr);

            User receiver = userRepository.findById(receiverId).orElse(null);
            if (receiver == null) return;

            messagingTemplate.convertAndSendToUser(
                    receiver.getUsername(),
                    "/queue/typing",
                    payload
            );
        } catch (Exception e) {
            // Ignore malformed payloads
        }
    }

    // ── FIX 3: Broadcast ──
    @MessageMapping("/chat.broadcast")
    public void broadcastMessage(@Payload ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/public", message);
    }

    // ── FIX 4: REST history endpoint ──
    @GetMapping("/api/messages/{recipientId}")
    @ResponseBody
    public ResponseEntity<List<ChatMessage>> getChatHistory(
            @PathVariable Long recipientId,
            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = authHeader.substring(7);
            String currentUserEmail = jwtUtil.extractEmail(token);

            User currentUser = userRepository.findByEmail(currentUserEmail).orElse(null);
            User recipient   = userRepository.findById(recipientId).orElse(null);

            if (currentUser == null || recipient == null) {
                return ResponseEntity.ok(List.of());
            }

            List<ChatMessage> messages = chatMessageRepository.findConversation(
                    currentUser.getUsername(),
                    recipient.getUsername()
            );

            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
    @GetMapping("/api/messages/unread/count")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getUnreadCount(Principal principal) {
        try {
            User currentUser = userRepository.findByEmail(principal.getName()).orElse(null);
            if (currentUser == null) return ResponseEntity.ok(Map.of("count", 0L));

            // Count messages where receiver = current user that are unread
            long count = chatMessageRepository.countUnreadMessages(currentUser.getUsername());
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("count", 0L));
        }
    }
    @PutMapping("/api/messages/{senderId}/read")
    @ResponseBody
    public ResponseEntity<Void> markAsRead(
            @PathVariable Long senderId,
            Principal principal) {
        User currentUser = userRepository.findByEmail(principal.getName()).orElse(null);
        if (currentUser == null) return ResponseEntity.ok().build();

        User sender = userRepository.findById(senderId).orElse(null);
        if (sender == null) return ResponseEntity.ok().build();

        chatMessageRepository.markMessagesAsRead(sender.getUsername(), currentUser.getUsername());
        return ResponseEntity.ok().build();
    }
}