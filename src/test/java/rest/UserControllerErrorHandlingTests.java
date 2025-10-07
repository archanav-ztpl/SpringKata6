package rest;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerErrorHandlingTests {

    @Autowired
    MockMvc mockMvc;

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
                .andExpect(jsonPath("$.path").value("/api/users/" + missingId))
                .andExpect(jsonPath("$.fieldErrors").isArray())
                .andExpect(jsonPath("$.fieldErrors.length()").value(0));
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

