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

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserDTO> getUsers() {
        List<User> users = userRepository.findAll();

        return users.stream().map(UserDTO::fromEntity).toList();
    }

    public Page<UserDTO> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(UserDTO::fromEntity);
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
        return UserDTO.fromEntity(user);
}

    @Transactional
    public UserDTO saveUser(CreateUserDTO userRequest) {
        User user = User.builder()
            .name(userRequest.getName())
            .email(userRequest.getEmail())
            .mobile(userRequest.getMobile())
            .age(userRequest.getAge())
        .build();

        User created = userRepository.save(user);
        return UserDTO.fromEntity(created);
    }

    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.deleteById(user.getId());
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public void saveAllUsers(List<CreateUserDTO> users) {
        List<User> userEntities = users.stream().map(userRequest -> User.builder()
            .name(userRequest.getName())
            .email(userRequest.getEmail())
            .mobile(userRequest.getMobile())
            .age(userRequest.getAge())
            .build()).toList();
        userRepository.saveAll(userEntities);
    }

}
