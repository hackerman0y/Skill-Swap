package com.skillswap.service;
import com.skillswap.config.JwtUtil;
import com.skillswap.entity.User;
import com.skillswap.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {


private final UserRepository userRepository ;
private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository, JwtUtil jwtUtil ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public User register(User u){
        u.setPassword(passwordEncoder.encode(u.getPassword()));
    return userRepository.save(u);
}

public User getUser(Long id){
    return userRepository.findById(id).orElseThrow();
}

    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Wrong Password");
        }
        return jwtUtil.generateToken(email);
    }

    public User updateProfile(String email, User request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (request.getUsername() != null) user.setUsername(request.getUsername());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        return userRepository.save(user);
    }
}
