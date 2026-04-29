package com.skillswap.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "chat_messages")
@Data
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Username of sender
    @Column(nullable = false)
    private String sender;

    // Username of receiver
    @Column(nullable = false)
    private String receiver;

    @Column(columnDefinition = "TEXT")
    private String content;

    // Alias field — frontend sends both "content" and "text"
    @Transient
    private String text;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @PrePersist
    public void prePersist() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        // Sync content/text fields
        if (content == null && text != null) {
            content = text;
        }
    }
}