package com.skillswap.controller;

import com.skillswap.config.JwtUtil;
import com.skillswap.entity.User;
import com.skillswap.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Map<String, Object>> login(@RequestBody User request) {
        String token = userService.login(request.getEmail(), request.getPassword());
        User user = userService.getUserByEmail(request.getEmail());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
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
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }

    // ─── User / Profile ──────────────────────────────────────────────────────

    // ── FIX 5: Return real user data instead of hardcoded fake data ──
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        try {
            User user = userService.getUser(id);  // ✅ was getAllUser(id)
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ── FIX 7: Get all users (used by chat-list.html) ──
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }

    @GetMapping("/profile/{id}")
    public User getProfile(@PathVariable Long id) {
        return userService.getUser(id);  // ✅ was getAllUser(id)
    }

    @GetMapping("/me")
    public ResponseEntity<User> getMe(@RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        User user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/settings")
    public ResponseEntity<User> updateProfile(
            @RequestBody User request,
            @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        User updated = userService.updateProfile(email, request);
        return ResponseEntity.ok(updated);
    }
}