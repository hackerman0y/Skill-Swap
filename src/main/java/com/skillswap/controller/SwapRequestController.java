package com.skillswap.controller;

import com.skillswap.config.JwtUtil;
import com.skillswap.dto.request.SwapRequestDTO;
import com.skillswap.dto.response.SwapResponseDTO;
import com.skillswap.entity.User;
import com.skillswap.service.SwapRequestService;
import com.skillswap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/swaps")
@RequiredArgsConstructor
public class SwapRequestController {

    private final SwapRequestService swapRequestService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    // ── FIX 7: Extract userId from JWT instead of trusting X-User-Id header ──
    private Long extractUserId(String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        User user = userService.getUserByEmail(email);
        return user.getId();
    }

    @PostMapping
    public ResponseEntity<SwapResponseDTO> sendRequest(
            @RequestBody SwapRequestDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(swapRequestService.sendSwapRequest(dto, userId));
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<SwapResponseDTO>> getIncoming(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(swapRequestService.getIncomingRequests(userId));
    }

    @GetMapping("/outgoing")
    public ResponseEntity<List<SwapResponseDTO>> getOutgoing(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(swapRequestService.getOutgoingRequests(userId));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<SwapResponseDTO> accept(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(swapRequestService.acceptRequest(id, userId));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<SwapResponseDTO> reject(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(swapRequestService.rejectRequest(id, userId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SwapResponseDTO> cancel(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(swapRequestService.cancelRequest(id, userId));
    }
}