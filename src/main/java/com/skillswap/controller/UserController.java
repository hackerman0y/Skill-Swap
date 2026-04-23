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
        // Return a confirmation response after clearing the security context
        return ResponseEntity.ok("Logged out successfully");
    }

    // ─── User / Profile ──────────────────────────────────────────────────────


    @GetMapping("/{id}")
    public Map<String, Object> getUser(@PathVariable Long id) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("name", "Hala");
        user.put("email", "hala@gmail.com");

        List<Map<String, String>> skills = new ArrayList<>();

        Map<String, String> s1 = new HashMap<>();
        s1.put("name", "Java");
        s1.put("type", "teach");

        Map<String, String> s2 = new HashMap<>();
        s2.put("name", "Design");
        s2.put("type", "learn");

        skills.add(s1);
        skills.add(s2);

        user.put("skills", skills);

        return user;
    }

    @GetMapping("/profile/{id}")
    public User getProfile(@PathVariable Long id) {
        return userService.getUser(id);
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
