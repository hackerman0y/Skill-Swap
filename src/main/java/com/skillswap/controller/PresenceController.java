package com.skillswap.controller;

import com.skillswap.service.PresenceService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/presence")
public class PresenceController {

    private final PresenceService presenceService;

    public PresenceController(PresenceService presenceService) {
        this.presenceService = presenceService;
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
}