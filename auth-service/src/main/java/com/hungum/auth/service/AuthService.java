package com.hungum.auth.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hungum.auth.kafka.KafkaProducerService;
import com.hungum.common.dto.AuthenticationResponse;
import com.hungum.common.dto.LoginRequestDto;
import com.hungum.common.dto.RegisterRequestDto;
import com.hungum.common.dto.UserDto;
import com.hungum.common.event.*;
import com.hungum.common.exceptions.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private TokenService tokenService;
    @Autowired
    private KafkaProducerService kafkaProducerService;
    @Value("${account.verification.url}")
    private String accountVerificationUrl;
    @Value("${google.client-id}")
    private String googleClientId;

    @Value("${google.client-secret}")
    private String googleClientSecret;

    public boolean existsByUserName(RegisterRequestDto registerRequestDto) {
        UserExistsEvent event = new UserExistsEvent(registerRequestDto);

        return kafkaProducerService.sendUserExistsEvent(event);
    }

    public void createUser(RegisterRequestDto registerRequestDto) {
        String encodedPassword = passwordEncoder.encode(registerRequestDto.getPassword());

        UserCreatedEvent event = new UserCreatedEvent(
                registerRequestDto.getUsername(),
                registerRequestDto.getEmail(),
                encodedPassword
        );

        kafkaProducerService.sendUserCreatedEvent(event);

        String token = generateTokenService(registerRequestDto);

        String message = "Thank you for signing up to Shop, please click on the below url to activate your account : "
                + accountVerificationUrl + "/" + token;

        SendMailEvent mailEvent = new SendMailEvent(registerRequestDto.getEmail(), "Account Activation", message);

        kafkaProducerService.sendSendMailEvent(mailEvent);

        log.info("Activation email sent via Kafka!");
    }

    private String generateTokenService(RegisterRequestDto registerRequestDto) {
        String token = UUID.randomUUID().toString();
        tokenService.saveVerificationToken(token, registerRequestDto.getUsername(), Duration.ofHours(24));
        return token;
    }

    public AuthenticationResponse authenticate(LoginRequestDto loginRequestDto) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authenticate);

        String username = loginRequestDto.getUsername();
        String accessToken = jwtTokenProvider.generateToken(authenticate);
        String refreshToken = jwtTokenProvider.generateRefreshToken(username, Duration.ofDays(7).toMillis());

        tokenService.saveRefreshToken(username, refreshToken, Duration.ofDays(7));

        return new AuthenticationResponse(accessToken, refreshToken, username);
    }

    public Optional<UserDto> getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.User springUser) {
            String username = springUser.getUsername();

            UserDetailsRequestEvent event = new UserDetailsRequestEvent(username);

            UserDetailsResponseEvent responseEvent = kafkaProducerService.sendUserDetailsRequest(event);

            if (responseEvent != null && responseEvent.getUserDto() != null) {
                UserDto userDto = responseEvent.getUserDto();
                return Optional.of(userDto);
            }
        }

        return Optional.empty();
    }

    public ApiResponse verifyAccount(String token) {
        Optional<String> usernameOpt = tokenService.getUsernameByToken(token);
        if (usernameOpt.isPresent()) {
            String username = usernameOpt.get();
            tokenService.deleteToken(token);
            return new ApiResponse(200, "User is Enabled");
        } else {
            return new ApiResponse(400, "Invalid Token");
        }
    }
    public String getUserFromGoogle(String code) throws JsonProcessingException {
        String googleTokenUrl = "https://oauth2.googleapis.com/token";
        RestTemplate restTemplate = new RestTemplate();

        String requestUrl = googleTokenUrl + "?code=" + code +
                "&client_id=" + googleClientId +
                "&client_secret=" + googleClientSecret +
                "&redirect_uri=http://localhost:8081/api/auth/login/oauth2/code/google" +
                "&grant_type=authorization_code";

        String response = restTemplate.postForObject(requestUrl, null, String.class);

        String username = extractUsernameFromGoogleResponse(response);

        return username;
    }

    public AuthenticationResponse generateJwtForGoogleUser(String username) {

        UserDetailsRequestEvent event = new UserDetailsRequestEvent(username);

        UserDetailsResponseEvent responseEvent = kafkaProducerService.sendUserDetailsRequest(event);

        UserDto userDto = null;
        if (responseEvent != null && responseEvent.getUserDto() != null) {
            userDto = responseEvent.getUserDto();
        }

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDto.getUsername(),
                null,
                null
        );

        String accessToken = jwtTokenProvider.generateToken(authentication);

        return new AuthenticationResponse(accessToken, null, userDto.getUsername());
    }

    private String extractUsernameFromGoogleResponse(String response) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);
        return rootNode.get("email").asText();
    }

}
