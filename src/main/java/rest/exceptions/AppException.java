package rest.exceptions;

import lombok.Getter;

@Getter
public abstract class AppException extends RuntimeException {
    private final String code;
    private final int status;

    protected AppException(String code, String message, int status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    protected AppException(String code, String message, int status, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.status = status;
    }

}

