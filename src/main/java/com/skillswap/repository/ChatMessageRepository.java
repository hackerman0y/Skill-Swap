package com.skillswap.repository;

import com.skillswap.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {


    @Query("""
        SELECT m FROM ChatMessage m
        WHERE (m.sender = :userA AND m.receiver = :userB)
           OR (m.sender = :userB AND m.receiver = :userA)
        ORDER BY m.timestamp ASC
    """)
    List<ChatMessage> findConversation(
            @Param("userA") String userA,
            @Param("userB") String userB
    );

    // Find all messages involving a user (for chat list)
    @Query("""
        SELECT m FROM ChatMessage m
        WHERE m.sender = :username OR m.receiver = :username
        ORDER BY m.timestamp DESC
    """)
    List<ChatMessage> findAllByUser(@Param("username") String username);
}