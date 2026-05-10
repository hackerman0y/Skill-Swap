package com.skillswap.controller;

import com.skillswap.config.JwtUtil;
import com.skillswap.dto.request.SessionRequestDTO;
import com.skillswap.dto.response.SessionResponseDTO;
import com.skillswap.entity.User;
import com.skillswap.service.SessionService;
import com.skillswap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    private Long extractUserId(String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        User user = userService.getUserByEmail(email);
        return user.getId();
    }

    // ── Schedule a session ──
    @PostMapping
    public ResponseEntity<SessionResponseDTO> schedule(
            @RequestBody SessionRequestDTO dto,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.scheduleSession(dto, userId));
    }

    // ── Get my sessions ──
    @GetMapping
    public ResponseEntity<List<SessionResponseDTO>> getMySessions(
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(sessionService.getMySessions(userId));
    }

    // ── Get session by id ──
    @GetMapping("/{id}")
    public ResponseEntity<SessionResponseDTO> getById(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(sessionService.getSessionById(id, userId));
    }

    // ── Start session: UPCOMING → IN_PROGRESS ──
    @PutMapping("/{id}/start")
    public ResponseEntity<SessionResponseDTO> startSession(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(sessionService.startSession(id, userId));
    }

    // ── End session: IN_PROGRESS → COMPLETED + 50 coins ──
    @PutMapping("/{id}/end")
    public ResponseEntity<SessionResponseDTO> endSession(
            @PathVariable Long id,
            @RequestHeader("Authorization") String authHeader) {
        Long userId = extractUserId(authHeader);
        return ResponseEntity.ok(sessionService.endSession(id, userId));
    }
}