package rest.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rest.dtos.CreateUserDTO;
import rest.dtos.UserDTO;
import rest.services.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users", description = "Fetch all users with pagination")
    @GetMapping(path = "/users")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @Parameter(description = "Pagination information", required = true)
            Pageable pageable) {
        logger.info("Fetching all users with pagination: {}", pageable);
        return ResponseEntity.ok().body(userService.getUsers(pageable));
    }

    @Operation(summary = "Get user by ID", description = "Fetch a user by their unique ID")
    @GetMapping(path = "/users/{id}")
    public ResponseEntity<UserDTO> getUserByIdPath(
            @Parameter(description = "ID of the user to fetch", required = true)
            @PathVariable Long id) {
        logger.info("Fetching user by ID: {}", id);
        return ResponseEntity.ok().body(userService.getUserById(id));
    }

    @Operation(summary = "Create a new user", description = "Save a new user to the database")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User details for the new user", required = true)
    @PostMapping(path = "/users")
    public ResponseEntity<UserDTO> saveUser(
            @RequestBody @Valid CreateUserDTO userRequest) {
        logger.info("Saving new user: {}", userRequest);
        return new ResponseEntity<>(userService.saveUser(userRequest), HttpStatus.CREATED);
    }

    @Operation(summary = "Delete user by ID", description = "Delete a user by their unique ID")
    @DeleteMapping(path = "/users/{id}")
    public ResponseEntity<Void> deleteUserByIdPath(
            @Parameter(description = "ID of the user to delete", required = true)
            @PathVariable Long id) {
        logger.info("Deleting user by ID: {}", id);
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
