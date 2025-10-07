package rest.controllers;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest.dtos.CreateUserDTO;
import rest.dtos.UserDTO;
import rest.services.UserService;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = "/users")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok().body(userService.getUsers(pageable));
    }

    @GetMapping(path = "/users/{id}")
    public ResponseEntity<UserDTO> getUserByIdPath(@PathVariable Long id) {
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @PostMapping(path = "/users")
    public ResponseEntity<UserDTO> saveUser(@RequestBody @Valid CreateUserDTO userRequest) {
        return new ResponseEntity<>(userService.saveUser(userRequest), HttpStatus.CREATED);
    }

    @DeleteMapping(path = "/users/{id}")
    public ResponseEntity<Void> deleteUserByIdPath(@PathVariable Long id) {
        userService.deleteUserById(id); // to throw 404 if not found
        return ResponseEntity.noContent().build();
    }
}
