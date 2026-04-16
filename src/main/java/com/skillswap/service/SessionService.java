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

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SwapRequestRepository swapRequestRepository;

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

    public List<SessionResponseDTO> getMySessions(Long userId) {
        List<Session> sent = sessionRepository.findBySwapRequestSenderId(userId);
        List<Session> received = sessionRepository.findBySwapRequestReceiverId(userId);
        sent.addAll(received);
        return sent.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public SessionResponseDTO getSessionById(Long sessionId, Long userId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found."));

        boolean isParticipant = session.getSwapRequest().getSender().getId().equals(userId)
                || session.getSwapRequest().getReceiver().getId().equals(userId);
        if (!isParticipant)
            throw new UnauthorizedException("You are not part of this session.");

        return toDTO(session);
    }

    private SessionResponseDTO toDTO(Session s) {
        return SessionResponseDTO.builder()
                .id(s.getId())
                .swapRequestId(s.getSwapRequest().getId())
                .scheduledAt(s.getScheduledAt())
                .status(s.getStatus())
                .createdAt(s.getCreatedAt())
                .build();
    }
}