package rest.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import rest.dtos.CreateUserDTO;
import rest.dtos.UserDTO;
import rest.entities.User;
import rest.exceptions.UserNotFoundException;
import rest.repositories.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("saveUser saves a valid user")
    void saveUserSavesValidUser() {
        CreateUserDTO createUserDTO = CreateUserDTO.builder()
            .name("John Doe")
            .email("john.doe@example.com")
            .mobile("1234567890")
            .age(30)
            .build();

        User user = User.builder()
            .id(1L)
            .name(createUserDTO.getName())
            .email(createUserDTO.getEmail())
            .mobile(createUserDTO.getMobile())
            .age(createUserDTO.getAge())
            .build();

        when(userRepository.findByEmail(createUserDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO savedUser = userService.saveUser(createUserDTO);

        assertNotNull(savedUser);
        assertEquals("John Doe", savedUser.getName());
        verify(userRepository, times(1)).findByEmail(createUserDTO.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("saveUser throws exception for duplicate email")
    void saveUserThrowsExceptionForDuplicateEmail() {
        CreateUserDTO createUserDTO = CreateUserDTO.builder()
            .name("John Doe")
            .email("john.doe@example.com")
            .mobile("1234567890")
            .age(30)
            .build();

        User existingUser = User.builder()
            .id(1L)
            .name("John Doe")
            .email("john.doe@example.com")
            .mobile("1234567890")
            .age(30)
            .build();

        when(userRepository.findByEmail(createUserDTO.getEmail())).thenReturn(Optional.of(existingUser));

        assertThrows(IllegalArgumentException.class, () -> userService.saveUser(createUserDTO));
        verify(userRepository, times(1)).findByEmail(createUserDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("deleteUserById deletes user for valid ID")
    void deleteUserByIdDeletesUserForValidId() {
        User user = User.builder()
            .id(1L)
            .name("John Doe")
            .email("john.doe@example.com")
            .mobile("1234567890")
            .age(30)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById(1L);

        userService.deleteUserById(1L);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("deleteUserById throws exception for invalid ID")
    void deleteUserByIdThrowsExceptionForInvalidId() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(999L));
        verify(userRepository, times(1)).findById(999L);
        verify(userRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("getUserById returns user for valid ID")
    void getUserByIdReturnsUserForValidId() {
        User user = User.builder()
            .id(1L)
            .name("John Doe")
            .email("john.doe@example.com")
            .mobile("1234567890")
            .age(30)
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals("John Doe", foundUser.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getUserById throws exception for invalid ID")
    void getUserByIdThrowsExceptionForInvalidId() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("getUsers returns paginated users")
    void getUsersReturnsPaginatedUsers() {
        User user1 = User.builder()
            .id(1L)
            .name("John Doe")
            .email("john.doe@example.com")
            .mobile("1234567890")
            .age(30)
            .build();

        User user2 = User.builder()
            .id(2L)
            .name("Jane Doe")
            .email("jane.doe@example.com")
            .mobile("0987654321")
            .age(25)
            .build();

        Page<User> userPage = new PageImpl<>(List.of(user1, user2));
        Pageable pageable = PageRequest.of(0, 2);

        when(userRepository.findAll(pageable)).thenReturn(userPage);

        Page<UserDTO> result = userService.getUsers(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getName());
        assertEquals("Jane Doe", result.getContent().get(1).getName());
        verify(userRepository, times(1)).findAll(pageable);
    }
}
