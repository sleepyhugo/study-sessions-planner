package com.hugo.studysessions.service;

import com.hugo.studysessions.dto.RegistrationDto;
import com.hugo.studysessions.model.User;
import com.hugo.studysessions.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // uses your BCrypt encoder

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public void registerNewUser(RegistrationDto form) {
        User user = new User();
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);
    }
}
