package com.skillswap.controller;

import com.skillswap.dto.request.SessionRequestDTO;
import com.skillswap.dto.response.SessionResponseDTO;
import com.skillswap.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<SessionResponseDTO> schedule(
            @RequestBody SessionRequestDTO dto,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(sessionService.scheduleSession(dto, userId));
    }

    @GetMapping
    public ResponseEntity<List<SessionResponseDTO>> getMySessions(
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(sessionService.getMySessions(userId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionResponseDTO> getById(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(sessionService.getSessionById(id, userId));
    }
}