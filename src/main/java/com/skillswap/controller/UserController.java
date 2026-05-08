package com.skillswap.controller;

import com.skillswap.config.JwtUtil;
import com.skillswap.entity.User;
import com.skillswap.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    // ─── Auth ────────────────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User request,
                                                     HttpServletResponse httpResponse) {
        String token = userService.login(request.getEmail(), request.getPassword());
        User user = userService.getUserByEmail(request.getEmail());

        Cookie cookie = new Cookie("auth_token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(7 * 24 * 3600);
        httpResponse.addCookie(cookie);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("role", user.getRole());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            return ResponseEntity.ok(userService.register(user));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse httpResponse) {
        Cookie cookie = new Cookie("auth_token", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        httpResponse.addCookie(cookie);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }

    // ─── User / Profile ──────────────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(userService.getUser(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/profile/{id}")
    public User getProfile(@PathVariable Long id) {
        return userService.getUser(id);
    }

    // ── Fixed: build a Map response so badge fields are included alongside user data ──
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getMe(
            @RequestHeader("Authorization") String authHeader) {

        String email = jwtUtil.extractEmail(authHeader.substring(7));
        User user = userService.getUserByEmail(email);

        Map<String, Object> response = new HashMap<>(); // ← was missing entirely
        response.put("id",              user.getId());
        response.put("username",        user.getUsername());
        response.put("email",           user.getEmail());
        response.put("role",            user.getRole());
        response.put("trustScore",      user.getTrustScore());
        response.put("online",          user.getOnline());
        response.put("activeBadgeId",   user.getActiveBadgeId());   // ← new
        response.put("activeBadgeName", user.getActiveBadgeName()); // ← new
        response.put("activeBadgeType", user.getActiveBadgeType()); // ← new

        return ResponseEntity.ok(response);
    }

    @PutMapping("/settings")
    public ResponseEntity<User> updateProfile(
            @RequestBody User request,
            @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        User updated = userService.updateProfile(email, request);
        return ResponseEntity.ok(updated);
    }

    // ─── Equip Badge ─────────────────────────────────────────────────────────

    @PutMapping("/equip/{itemId}")
    public ResponseEntity<?> equipItem(@PathVariable Long itemId,
                                       @RequestHeader("Authorization") String authHeader) {
        try {
            String email = jwtUtil.extractEmail(authHeader.substring(7));
            User user = userService.getUserByEmail(email);

            User updated = userService.equipItem(user.getId(), itemId);

            Map<String, Object> response = new HashMap<>();
            response.put("activeBadgeId",   updated.getActiveBadgeId());
            response.put("activeBadgeName", updated.getActiveBadgeName());
            response.put("activeBadgeType", updated.getActiveBadgeType());
            response.put("message", updated.getActiveBadgeId() == null
                    ? "Badge unequipped"
                    : "\"" + updated.getActiveBadgeName() + "\" equipped!");

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}