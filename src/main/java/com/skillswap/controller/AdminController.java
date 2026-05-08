package com.skillswap.controller;

import com.skillswap.repository.UserRepository;
import com.skillswap.repository.SkillRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public AdminController(UserRepository userRepository, SkillRepository skillRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        long totalAdmins = userRepository.findAll().stream()
                .filter(u -> "ADMIN".equals(u.getRole())).count();
        long onlineUsers = userRepository.findAll().stream()
                .filter(u -> Boolean.TRUE.equals(u.getOnline())).count();

        return ResponseEntity.ok(Map.of(
                "totalUsers",  userRepository.count(),
                "totalSkills", skillRepository.count(),
                "totalAdmins", totalAdmins,
                "onlineUsers", onlineUsers
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    @DeleteMapping("/skills/{id}")
    public ResponseEntity<?> deleteSkill(@PathVariable Long id) {
        skillRepository.deleteById(id);
        return ResponseEntity.ok("Deleted");
    }

    @PatchMapping("/users/{id}/role")
    public ResponseEntity<?> changeRole(@PathVariable Long id, @RequestBody Map<String, String> body) {
        return userRepository.findById(id).map(user -> {
            user.setRole(body.get("role"));
            userRepository.save(user);
            return ResponseEntity.ok("Role updated");
        }).orElse(ResponseEntity.notFound().build());
    }
}