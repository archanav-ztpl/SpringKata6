package rest.exceptions;

public class UserNotFoundException extends AppException {
    public UserNotFoundException(Long id) {
        super("USER_NOT_FOUND", "No user by ID: " + id, 404);
    }
    public UserNotFoundException(Long id, Throwable cause) {
        super("USER_NOT_FOUND", "No user by ID: " + id, 404, cause);
    }
}
