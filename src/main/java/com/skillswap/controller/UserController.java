package com.skillswap.controller;

import com.skillswap.config.JwtUtil;
import com.skillswap.entity.User;
import com.skillswap.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User request) {
        String token = userService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok( token);
    }

    @PostMapping("/register")
    public User register(@RequestBody User request) {
        return userService.register(request);
    }

    @GetMapping("/profile/{id}")
    public User getProfile(@PathVariable Long id){
        return userService.getUser(id);
    }

    @PutMapping("/settings")
    public ResponseEntity<User> updateProfile(@RequestBody User request,
                                              @RequestHeader("Authorization") String token) {
        String email = jwtUtil.extractEmail(token.substring(7));
        User updated = userService.updateProfile(email, request);
        return ResponseEntity.ok(updated);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("Logged out successfully");
    }

}
