package rest.controllers;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /api/users - Fetch all users with pagination")
    void getAllUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/users/{id} - Fetch user by ID")
    void getUserById() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 1)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST /api/users - Create a new user")
    void saveUser() throws Exception {
        String userJson = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\",\"mobile\":\"1234567890\",\"age\":30}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Delete user by ID")
    void deleteUserById() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 1))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/users/{id} - Fetch user by non-existent ID")
    void getUserByNonExistentId() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 999)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/users/{id} - Fetch user with invalid ID format")
    void getUserByInvalidIdFormat() throws Exception {
        mockMvc.perform(get("/api/users/{id}", "invalid")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users - Create user with missing fields")
    void saveUserWithMissingFields() throws Exception {
        String userJson = "{\"email\":\"john.doe@example.com\"}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users - Create user with invalid email")
    void saveUserWithInvalidEmail() throws Exception {
        String userJson = "{\"name\":\"John Doe\",\"email\":\"invalid-email\",\"mobile\":\"1234567890\",\"age\":30}";

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Delete user by non-existent ID")
    void deleteUserByNonExistentId() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", 999))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /api/users/{id} - Delete user with invalid ID format")
    void deleteUserByInvalidIdFormat() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", "invalid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/users/{id} returns structured USER_NOT_FOUND error")
    void userNotFoundReturnsStructuredError() throws Exception {
        long missingId = 9999L;
        mockMvc.perform(get("/api/users/{id}", missingId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("USER_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("No user by ID: " + missingId))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.path").value("/api/users/" + missingId));
    }

    @Test
    @DisplayName("POST /api/users with invalid body returns VALIDATION_FAILED and field errors")
    void validationErrorsReturnStructuredResponse() throws Exception {
        String invalidPayload = "{" +
                "\"name\":\"Al\"," +              // too short
                "\"email\":\"bad-email\"," +        // invalid email
                "\"mobile\":\"12345\"," +          // not 10 digits
                "\"age\":0" +                         // below min
                "}";

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors.length()").value(Matchers.greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.fieldErrors[*].field", Matchers.hasItems("name", "mobile", "age")));
    }
}
