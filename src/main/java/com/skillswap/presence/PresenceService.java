package com.skillswap.presence;

import com.skillswap.entity.User;
import com.skillswap.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class PresenceService {

    private final UserRepository userRepository;

    public PresenceService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void setOnline(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setOnline(true);
        userRepository.save(user);
    }

    public void setOffline(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setOnline(false);
        userRepository.save(user);
    }
}