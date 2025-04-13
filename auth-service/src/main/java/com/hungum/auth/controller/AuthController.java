package com.hungum.auth.controller;

import com.hungum.common.dto.AuthenticationResponse;
import com.hungum.common.dto.LoginRequestDto;
import com.hungum.common.dto.RegisterRequestDto;
import com.hungum.common.dto.UserDto;
import com.hungum.common.exceptions.ApiResponse;
import com.hungum.auth.service.AuthService;
import com.hungum.auth.service.JwtTokenProvider;
import com.hungum.auth.service.TokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth/")
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private TokenService tokenService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequestDto loginRequestDto) {
        return new ResponseEntity<>(authService.authenticate(loginRequestDto), HttpStatus.OK);
    }

    @PostMapping("register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequestDto registerRequestDto) {
        if (authService.existsByUserName(registerRequestDto)) {
            return new ResponseEntity<>(new ApiResponse(400, "Username already exists"), HttpStatus.BAD_REQUEST);
        }

        authService.createUser(registerRequestDto);
        return new ResponseEntity<>(new ApiResponse(200, "User Registration Completed Successfully!!"), HttpStatus.OK);
    }

    @PostMapping("refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        try {
            String username = jwtTokenProvider.getUsernameFromJWT(refreshToken);
            Optional<String> storedRefreshToken = tokenService.getRefreshToken(username);

            if (storedRefreshToken.isPresent() && storedRefreshToken.get().equals(refreshToken)) {
                String newAccessToken = jwtTokenProvider.generateRefreshToken(username, Duration.ofMinutes(15).toMillis());
                return ResponseEntity.ok(new AuthenticationResponse(newAccessToken, refreshToken, username));
            } else {
                return ResponseEntity.status(401).body(null);
            }
        } catch (Exception e) {
            return ResponseEntity.status(401).body(null);
        }
    }

    @GetMapping("accountVerification/{token}")
    public ApiResponse verifyAccount(@PathVariable String token) {
        return authService.verifyAccount(token);
    }

    @GetMapping("login/oauth2/code/google")
    public ResponseEntity<AuthenticationResponse> googleLoginCallback(@RequestParam("code") String code) {
        try {

            String username = authService.getUserFromGoogle(code);

            AuthenticationResponse authResponse = authService.generateJwtForGoogleUser(username);

            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            log.error("Google Login failed: {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}
