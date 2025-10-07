package rest.dtos;

import lombok.Builder;
import lombok.Data;

import jakarta.validation.constraints.*;

@Data
@Builder
public class CreateUserDTO {

    @NotBlank(message = "Invalid Name: Empty name")
    @Size(min = 3, max = 30, message = "Invalid Name: min 3 to max 30 characters")
    String name;

    @NotBlank(message = "Invalid Email: Empty email")
    @Email(message = "Invalid email")
    String email;

    @NotBlank(message = "Invalid Phone number: Empty number")
    @Pattern(regexp = "^\\d{10}$", message = "Invalid phone number")
    String mobile;

    @NotNull(message = "Invalid Age: Age is NULL")
    @Min(value = 1, message = "Invalid Age: Equals to zero")
    @Max(value = 100, message = "Invalid Age: Exceeds 100 years")
    Integer age;

}
