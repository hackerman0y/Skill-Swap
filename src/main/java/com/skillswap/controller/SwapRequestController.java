package com.skillswap.controller;

import com.skillswap.dto.request.SwapRequestDTO;
import com.skillswap.dto.response.SwapResponseDTO;
import com.skillswap.service.SwapRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/swaps")
@RequiredArgsConstructor
public class SwapRequestController {

    private final SwapRequestService swapRequestService;

    // Temporary: pass userId as header until JWT is ready
    @PostMapping
    public ResponseEntity<SwapResponseDTO> sendRequest(
            @RequestBody SwapRequestDTO dto,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(swapRequestService.sendSwapRequest(dto, userId));
    }

    @GetMapping("/incoming")
    public ResponseEntity<List<SwapResponseDTO>> getIncoming(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(swapRequestService.getIncomingRequests(userId));
    }

    @GetMapping("/outgoing")
    public ResponseEntity<List<SwapResponseDTO>> getOutgoing(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(swapRequestService.getOutgoingRequests(userId));
    }

    @PatchMapping("/{id}/accept")
    public ResponseEntity<SwapResponseDTO> accept(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(swapRequestService.acceptRequest(id, userId));
    }

    @PatchMapping("/{id}/reject")
    public ResponseEntity<SwapResponseDTO> reject(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(swapRequestService.rejectRequest(id, userId));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<SwapResponseDTO> cancel(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(swapRequestService.cancelRequest(id, userId));
    }
}