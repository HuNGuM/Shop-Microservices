import com.hungum.auth.kafka.KafkaProducerService;
import com.hungum.auth.service.AuthService;
import com.hungum.auth.service.JwtTokenProvider;
import com.hungum.auth.service.TokenService;
import com.hungum.common.dto.LoginRequestDto;
import com.hungum.common.dto.AuthenticationResponse;
import com.hungum.common.dto.RegisterRequestDto;
import com.hungum.common.exceptions.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private TokenService tokenService;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testVerifyAccount() {
        // Arrange
        String token = "token";
        when(tokenService.getUsernameByToken(token)).thenReturn(Optional.of("user"));

        // Act
        ApiResponse response = authService.verifyAccount(token);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals("User is Enabled", response.getMessage());
    }

}
