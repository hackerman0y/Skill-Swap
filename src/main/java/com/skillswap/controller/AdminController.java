package com.skillswap.controller;

import com.skillswap.repository.SwapRequestRepository;
import com.skillswap.repository.UserRepository;
import com.skillswap.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;
    private final SwapRequestRepository swapRequestRepository;

    // ← NO constructor here, Lombok generates it

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
        if (!skillRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        swapRequestRepository.deleteByOfferedSkillId(id);
        swapRequestRepository.deleteByWantedSkillId(id);
        skillRepository.deleteById(id);
        return ResponseEntity.ok("Skill deleted successfully");
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