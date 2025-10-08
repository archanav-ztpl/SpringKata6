package rest.services;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import rest.dtos.CreateUserDTO;
import rest.dtos.UserDTO;
import rest.entities.User;
import rest.exceptions.UserNotFoundException;
import rest.repositories.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Page<UserDTO> getUsers(Pageable pageable) {
        logger.info("Fetching users with pageable: {}", pageable);
        return userRepository.findAll(pageable).map(UserDTO::fromEntity);
    }

    public UserDTO getUserById(Long id) {
        logger.info("Fetching user by ID: {}", id);
        return userRepository.findById(id)
                .map(user -> {
                    logger.info("User found: ID={}, Name={}", user.getId(), user.getName());
                    return UserDTO.fromEntity(user);
                })
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public UserDTO saveUser(CreateUserDTO userRequest) {
        logger.info("Saving new user: email: {}", userRequest.getEmail());
        Optional<User> existing = userRepository.findByEmail(userRequest.getEmail());
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + userRequest.getEmail());
        }
        User user = User.builder()
            .name(userRequest.getName())
            .email(userRequest.getEmail())
            .mobile(userRequest.getMobile())
            .age(userRequest.getAge())
        .build();

        User created = userRepository.save(user);
        logger.info("User created: ID: {}, name: {}", created.getId(), created.getName());
        return UserDTO.fromEntity(created);
    }

    public void deleteUserById(Long id) {
        logger.info("Deleting user by ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(user.getId());
        logger.info("User ID: {} deleted", id);
    }

    public void deleteAllUsers() {
        logger.info("Deleting all users");
        userRepository.deleteAll();
    }

    public void saveAllUsers(List<CreateUserDTO> users) {
        logger.info("Saving multiple users: {}", users);
        List<User> userEntities = users.stream().map(userRequest -> User.builder()
            .name(userRequest.getName())
            .email(userRequest.getEmail())
            .mobile(userRequest.getMobile())
            .age(userRequest.getAge())
            .build()).toList();
        userRepository.saveAll(userEntities);
    }

}
