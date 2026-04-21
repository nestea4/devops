package com.example.userservice.service;

import com.example.userservice.exeption.DuplicateResourceException;
import com.example.userservice.exeption.ResourceNotFoundException;
import com.example.userservice.model.User;
import com.example.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("1234567890");
    }

    // findAll

    @Test
    void findAll_ShouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<User> result = userService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void findAll_WhenEmpty_ShouldReturnEmptyList() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<User> result = userService.findAll();

        assertThat(result).isEmpty();
    }

    //  findById

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertThat(result.getId()).isEqualTo(1L);
        // неправильне значення тест впаде
        //assertThat(result.getEmail()).isEqualTo("WRONG_EMAIL@example.com");
        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findById_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // findByEmail

    @Test
    void findByEmail_WhenUserExists_ShouldReturnUser() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

        User result = userService.findByEmail("test@example.com");

        assertThat(result.getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void findByEmail_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail("unknown@example.com"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("unknown@example.com");
    }

    // save

    @Test
    void save_ShouldSaveAndReturnUser() {
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.save(user);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("test@example.com");
        verify(userRepository, times(1)).save(user);
    }

    //  update

    @Test
    void update_WhenEmailNotChanged_ShouldUpdateUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        User updatedUser = new User();
        updatedUser.setEmail("test@example.com"); // той самий email
        updatedUser.setFirstName("Jane");
        updatedUser.setLastName("Doe");
        updatedUser.setPassword("newpassword");

        User result = userService.update(1L, updatedUser);

        assertThat(result).isNotNull();
        verify(userRepository, times(1)).save(updatedUser);
    }

    @Test
    void update_WhenEmailChangedAndAlreadyExists_ShouldThrowDuplicateResourceException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("other@example.com")).thenReturn(true);

        User updatedUser = new User();
        updatedUser.setEmail("other@example.com"); // новий email який вже існує

        assertThatThrownBy(() -> userService.update(1L, updatedUser))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("other@example.com");
    }

    @Test
    void update_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, user))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    //  deleteById

    @Test
    void deleteById_WhenUserExists_ShouldDelete() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteById_WhenUserNotFound_ShouldThrowResourceNotFoundException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");

        verify(userRepository, never()).deleteById(any());
    }

    // existsByEmail

    @Test
    void existsByEmail_WhenExists_ShouldReturnTrue() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        boolean result = userService.existsByEmail("test@example.com");

        assertThat(result).isTrue();
    }

    @Test
    void existsByEmail_WhenNotExists_ShouldReturnFalse() {
        when(userRepository.existsByEmail("nobody@example.com")).thenReturn(false);

        boolean result = userService.existsByEmail("nobody@example.com");

        assertThat(result).isFalse();
    }
}