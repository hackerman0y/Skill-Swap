package com.skillswap.controller;

import com.skillswap.entity.Notification;
import com.skillswap.entity.User;
import com.skillswap.service.NotificationService;
import com.skillswap.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    public NotificationController(NotificationService notificationService,
                                  UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getNotifications(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(notificationService.getNotifications(user.getId()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Integer> getUnreadCount(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(notificationService.countUnread(user.getId()));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok().build();
    }
}