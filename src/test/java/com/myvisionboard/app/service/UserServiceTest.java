package com.myvisionboard.app.service;

import com.myvisionboard.app.model.User;
import com.myvisionboard.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id("user-1")
                .name("John Doe")
                .email("john@example.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    void findByEmail_WhenUserExists_ReturnsUser() {
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.findByEmail("john@example.com");

        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo("john@example.com");
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void findByEmail_WhenUserDoesNotExist_ReturnsEmpty() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByEmail("unknown@example.com");

        assertThat(result).isEmpty();
        verify(userRepository).findByEmail("unknown@example.com");
    }

    @Test
    void existsByEmail_WhenEmailExists_ReturnsTrue() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("john@example.com");

        assertThat(result).isTrue();
        verify(userRepository).existsByEmail("john@example.com");
    }

    @Test
    void existsByEmail_WhenEmailDoesNotExist_ReturnsFalse() {
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);

        boolean result = userService.existsByEmail("new@example.com");

        assertThat(result).isFalse();
        verify(userRepository).existsByEmail("new@example.com");
    }

    @Test
    void save_PersistsAndReturnsUser() {
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.save(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("user-1");
        assertThat(result.getName()).isEqualTo("John Doe");
        verify(userRepository).save(user);
    }

    @Test
    void deleteById_DelegatesDeleteToRepository() {
        doNothing().when(userRepository).deleteById("user-1");

        userService.deleteById("user-1");

        verify(userRepository).deleteById("user-1");
    }
}
