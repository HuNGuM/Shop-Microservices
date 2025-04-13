package com.hungum.user.controller;

import com.hungum.common.dto.RegisterRequestDto;
import com.hungum.user.model.User;
import com.hungum.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Void> createUser(@RequestBody RegisterRequestDto dto) {
        User user = new User(dto.getEmail(), dto.getUsername(), dto.getPassword());
        user.setEnabled(false);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exists")
    public boolean existsByUsername(@RequestParam String username) {
        return userRepository.existsByUsername(username);
    }
}
