package com.skillswap.service;

import com.skillswap.dto.request.SessionRequestDTO;
import com.skillswap.dto.response.SessionResponseDTO;
import com.skillswap.entity.*;
import com.skillswap.enums.SessionStatus;
import com.skillswap.enums.SwapStatus;
import com.skillswap.exception.*;
import com.skillswap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SwapRequestRepository swapRequestRepository;
    private final CoinService coinService;

    // ── Schedule a new session ──
    public SessionResponseDTO scheduleSession(SessionRequestDTO dto, Long userId) {
        SwapRequest swap = swapRequestRepository.findById(dto.getSwapRequestId())
                .orElseThrow(() -> new ResourceNotFoundException("Swap request not found."));

        if (swap.getStatus() != SwapStatus.ACCEPTED)
            throw new IllegalStateException("Cannot schedule a session for a non-accepted swap.");

        boolean isParticipant = swap.getSender().getId().equals(userId)
                || swap.getReceiver().getId().equals(userId);
        if (!isParticipant)
            throw new UnauthorizedException("You are not part of this swap.");

        boolean alreadyScheduled = sessionRepository.findBySwapRequestId(dto.getSwapRequestId()).isPresent();
        if (alreadyScheduled)
            throw new IllegalStateException("A session is already scheduled for this swap.");

        Session session = Session.builder()
                .swapRequest(swap)
                .scheduledAt(dto.getScheduledAt())
                .status(SessionStatus.UPCOMING)
                .build();

        return toDTO(sessionRepository.save(session));
    }

    // ── Get all sessions for a user ──
    public List<SessionResponseDTO> getMySessions(Long userId) {
        List<Session> sent     = sessionRepository.findBySwapRequestSenderId(userId);
        List<Session> received = sessionRepository.findBySwapRequestReceiverId(userId);
        sent.addAll(received);
        return sent.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // ── Get single session by id ──
    public SessionResponseDTO getSessionById(Long sessionId, Long userId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found."));

        boolean isParticipant = session.getSwapRequest().getSender().getId().equals(userId)
                || session.getSwapRequest().getReceiver().getId().equals(userId);
        if (!isParticipant)
            throw new UnauthorizedException("You are not part of this session.");

        return toDTO(session);
    }

    // ── START session: UPCOMING → IN_PROGRESS (publisher only) ──
    public SessionResponseDTO startSession(Long sessionId, Long userId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found."));

        Long senderId = session.getSwapRequest().getSender().getId();
        if (!senderId.equals(userId))
            throw new UnauthorizedException("Only the session publisher can start this session.");

        if (session.getStatus() != SessionStatus.UPCOMING)
            throw new IllegalStateException("Session must be UPCOMING to start.");

        session.setStatus(SessionStatus.IN_PROGRESS);
        session.setStartedAt(LocalDateTime.now());
        sessionRepository.save(session);

        return toDTO(session);
    }

    // ── END session: IN_PROGRESS → COMPLETED + earn 50 coins (publisher only) ──
    public SessionResponseDTO endSession(Long sessionId, Long userId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found."));

        Long senderId   = session.getSwapRequest().getSender().getId();
        Long receiverId = session.getSwapRequest().getReceiver().getId(); // fetch before save

        if (!senderId.equals(userId))
            throw new UnauthorizedException("Only the session publisher can end this session.");

        if (session.getStatus() != SessionStatus.IN_PROGRESS)
            throw new IllegalStateException("Session must be IN_PROGRESS to end. Start it first.");

        session.setStatus(SessionStatus.COMPLETED);
        session.setCompletedAt(LocalDateTime.now());
        sessionRepository.save(session);

        // ── Award coins to both AFTER save ──
        coinService.earnCoins(senderId,   50, "Completed a teaching session");
        coinService.earnCoins(receiverId, 50, "Completed a learning session");

        return toDTO(session);
    }
    // ── Map to DTO ──
    private SessionResponseDTO toDTO(Session s) {
        return SessionResponseDTO.builder()
                .id(s.getId())
                .swapRequestId(s.getSwapRequest().getId())
                .scheduledAt(s.getScheduledAt())
                .status(s.getStatus())
                .createdAt(s.getCreatedAt())
                .startedAt(s.getStartedAt())
                .completedAt(s.getCompletedAt())
                .senderUserId(s.getSwapRequest().getSender().getId())
                .senderUsername(s.getSwapRequest().getSender().getUsername())
                .receiverUsername(s.getSwapRequest().getReceiver().getUsername())
                .build();
    }
}