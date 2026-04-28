package com.skillswap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ChatViewController {


    @GetMapping("/chat")
    public String chatList() {
        return "forward:/chat-list.html";
    }


    @GetMapping("/chat/{userId}")
    public String chatRoom(@PathVariable String userId) {
        return "forward:/chat.html";
    }


    @GetMapping("/notifications")
    public String notifications() {
        return "forward:/notifications.html";
    }
}