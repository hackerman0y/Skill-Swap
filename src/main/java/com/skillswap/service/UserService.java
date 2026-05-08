package com.skillswap.service;

import com.skillswap.config.JwtUtil;
import com.skillswap.entity.StoreItem;
import com.skillswap.entity.User;
import com.skillswap.repository.StoreItemRepository;
import com.skillswap.repository.UserInventoryRepository;
import com.skillswap.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UserInventoryRepository userInventoryRepository; // ← injected, not static
    private final StoreItemRepository storeItemRepository;         // ← injected, not static

    public UserService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       JwtUtil jwtUtil,
                       UserInventoryRepository userInventoryRepository,
                       StoreItemRepository storeItemRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.userInventoryRepository = userInventoryRepository;
        this.storeItemRepository = storeItemRepository;
    }

    public User register(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            if (user.getTrustScore() == null) user.setTrustScore(0);
            if (user.getOnline() == null) user.setOnline(false);
            if (user.getRole() == null) user.setRole("USER");
            return userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public User getUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Wrong Password");
        }
        return jwtUtil.generateToken(email, user.getRole());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    public User updateProfile(String email, User request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        return userRepository.save(user);
    }

    public User equipItem(Long userId, Long itemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Toggle off — clicking the same equipped item unequips it
        if (itemId.equals(user.getActiveBadgeId())) {
            user.setActiveBadgeId(null);
            user.setActiveBadgeName(null);
            user.setActiveBadgeType(null);
            return userRepository.save(user);
        }

        // Verify the user actually owns this item
        boolean owns = userInventoryRepository.existsByUserIdAndItemId(userId, itemId); // ← instance call
        if (!owns) {
            throw new RuntimeException("Item not in inventory");
        }

        StoreItem item = storeItemRepository.findById(itemId) // ← instance call
                .orElseThrow(() -> new RuntimeException("Store item not found"));

        user.setActiveBadgeId(item.getId());
        user.setActiveBadgeName(item.getName());
        user.setActiveBadgeType(item.getCategory());

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}