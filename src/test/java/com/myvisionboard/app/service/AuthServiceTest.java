package com.myvisionboard.app.service;

import com.myvisionboard.app.dto.request.LoginRequest;
import com.myvisionboard.app.dto.request.RegisterRequest;
import com.myvisionboard.app.dto.response.AuthResponse;
import com.myvisionboard.app.model.User;
import com.myvisionboard.app.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("John Doe");
        registerRequest.setEmail("john@example.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@example.com");
        loginRequest.setPassword("password123");

        user = User.builder()
                .id("user-1")
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    void register_WhenEmailNotTaken_ReturnsAuthResponse() {
        when(userService.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userService.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken("john@example.com")).thenReturn("jwt-token");

        AuthResponse result = authService.register(registerRequest);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(userService).existsByEmail("john@example.com");
        verify(passwordEncoder).encode("password123");
        verify(userService).save(any(User.class));
        verify(jwtService).generateToken("john@example.com");
    }

    @Test
    void register_WhenEmailAlreadyRegistered_ThrowsRuntimeException() {
        when(userService.existsByEmail("john@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Email already registered.");

        verify(userService).existsByEmail("john@example.com");
        verify(userService, never()).save(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_WhenCredentialsAreValid_ReturnsAuthResponse() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.findByEmail("john@example.com")).thenReturn(Optional.of(user));
        when(jwtService.generateToken("john@example.com")).thenReturn("jwt-token");

        AuthResponse result = authService.login(loginRequest);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt-token");
        assertThat(result.getName()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).findByEmail("john@example.com");
        verify(jwtService).generateToken("john@example.com");
    }

    @Test
    void login_WhenCredentialsAreInvalid_ThrowsBadCredentialsException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(BadCredentialsException.class);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, never()).findByEmail(any());
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_WhenUserNotFoundAfterAuth_ThrowsRuntimeException() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(userService.findByEmail("john@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found.");

        verify(jwtService, never()).generateToken(any());
    }
}
