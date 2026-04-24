package com.skillswap.controller;

import com.skillswap.entity.User;
import com.skillswap.repository.UserRepository;
import com.skillswap.service.PresenceService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/presence")
public class PresenceController {

    private final PresenceService presenceService;
    private final UserRepository userRepository;

    public PresenceController(PresenceService presenceService, UserRepository userRepository) {
        this.presenceService = presenceService;
        this.userRepository = userRepository;
    }

    @PostMapping("/online")
    public void setOnline() {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return;
        }

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        presenceService.setOnline(email);
    }

    @PostMapping("/offline")
    public void setOffline() {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return;
        }

        String email = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        presenceService.setOffline(email);
    }

    @PostMapping("/offline/{userId}")
    public void setOfflineById(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setOnline(false);
        userRepository.save(user);
    }
}