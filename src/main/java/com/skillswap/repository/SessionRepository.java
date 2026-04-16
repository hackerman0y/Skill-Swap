package com.skillswap.repository;

import com.skillswap.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findBySwapRequestSenderId(Long userId);
    List<Session> findBySwapRequestReceiverId(Long userId);
    Optional<Session> findBySwapRequestId(Long swapRequestId);
}
