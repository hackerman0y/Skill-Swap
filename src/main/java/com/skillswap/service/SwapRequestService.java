package com.skillswap.service;

import com.skillswap.dto.request.SwapRequestDTO;
import com.skillswap.dto.response.SwapResponseDTO;
import com.skillswap.entity.*;
import com.skillswap.enums.SwapStatus;
import com.skillswap.exception.*;
import com.skillswap.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SwapRequestService {

    private final SwapRequestRepository swapRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public SwapResponseDTO sendSwapRequest(SwapRequestDTO dto, Long senderId) {
        if (senderId.equals(dto.getReceiverId()))
            throw new IllegalStateException("You cannot send a swap request to yourself.");

        boolean alreadyPending = swapRequestRepository
                .existsBySenderIdAndReceiverIdAndStatus(senderId, dto.getReceiverId(), SwapStatus.PENDING);
        if (alreadyPending)
            throw new IllegalStateException("You already have a pending request with this user.");

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("Sender not found."));
        User receiver = userRepository.findById(dto.getReceiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found."));
        Skill offeredSkill = skillRepository.findById(dto.getOfferedSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Offered skill not found."));
        Skill wantedSkill = skillRepository.findById(dto.getWantedSkillId())
                .orElseThrow(() -> new ResourceNotFoundException("Wanted skill not found."));

        SwapRequest swapRequest = SwapRequest.builder()
                .sender(sender)
                .receiver(receiver)
                .offeredSkill(offeredSkill)
                .wantedSkill(wantedSkill)
                .status(SwapStatus.PENDING)
                .build();

        return toDTO(swapRequestRepository.save(swapRequest));
    }

    public List<SwapResponseDTO> getIncomingRequests(Long userId) {
        return swapRequestRepository.findByReceiverId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<SwapResponseDTO> getOutgoingRequests(Long userId) {
        return swapRequestRepository.findBySenderId(userId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    public SwapResponseDTO acceptRequest(Long swapId, Long userId) {
        SwapRequest swap = getSwapOrThrow(swapId);
        if (!swap.getReceiver().getId().equals(userId))
            throw new UnauthorizedException("Only the receiver can accept this request.");
        if (swap.getStatus() != SwapStatus.PENDING)
            throw new IllegalStateException("Only pending requests can be accepted.");
        swap.setStatus(SwapStatus.ACCEPTED);
        return toDTO(swapRequestRepository.save(swap));
    }

    public SwapResponseDTO rejectRequest(Long swapId, Long userId) {
        SwapRequest swap = getSwapOrThrow(swapId);
        if (!swap.getReceiver().getId().equals(userId))
            throw new UnauthorizedException("Only the receiver can reject this request.");
        if (swap.getStatus() != SwapStatus.PENDING)
            throw new IllegalStateException("Only pending requests can be rejected.");
        swap.setStatus(SwapStatus.REJECTED);
        return toDTO(swapRequestRepository.save(swap));
    }

    public SwapResponseDTO cancelRequest(Long swapId, Long userId) {
        SwapRequest swap = getSwapOrThrow(swapId);
        if (!swap.getSender().getId().equals(userId))
            throw new UnauthorizedException("Only the sender can cancel this request.");
        if (swap.getStatus() != SwapStatus.PENDING)
            throw new IllegalStateException("Only pending requests can be cancelled.");
        swap.setStatus(SwapStatus.CANCELLED);
        return toDTO(swapRequestRepository.save(swap));
    }

    private SwapRequest getSwapOrThrow(Long swapId) {
        return swapRequestRepository.findById(swapId)
                .orElseThrow(() -> new ResourceNotFoundException("Swap request not found."));
    }

    private SwapResponseDTO toDTO(SwapRequest s) {
        return SwapResponseDTO.builder()
                .id(s.getId())
                .senderId(s.getSender().getId())
                .senderUsername(s.getSender().getUsername())
                .receiverId(s.getReceiver().getId())
                .receiverUsername(s.getReceiver().getUsername())
                .offeredSkillId(s.getOfferedSkill().getId())
                .offeredSkillName(s.getOfferedSkill().getName())
                .wantedSkillId(s.getWantedSkill().getId())
                .wantedSkillName(s.getWantedSkill().getName())
                .status(s.getStatus())
                .createdAt(s.getCreatedAt())
                .build();
    }
}