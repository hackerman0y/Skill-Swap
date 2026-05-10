package com.skillswap.repository;

import com.skillswap.entity.SwapRequest;
import com.skillswap.enums.SwapStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

public interface SwapRequestRepository extends JpaRepository<SwapRequest, Long> {

    // ── Used by SwapRequestService ──
    boolean existsBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, SwapStatus status);

    List<SwapRequest> findByReceiverId(Long receiverId);

    List<SwapRequest> findBySenderId(Long senderId);

    // ── Used by AdminController ──
    @Transactional
    void deleteByOfferedSkillId(Long skillId);

    @Transactional
    void deleteByWantedSkillId(Long skillId);
}