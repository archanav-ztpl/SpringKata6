package rest.error;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String path,
        String code,
        String message,
        List<FieldErrorEntry> fieldErrors
) {
    public static ErrorResponse of(int status, String error, String path, String code, String message, List<FieldErrorEntry> fieldErrors) {
        return new ErrorResponse(
                Instant.now(),
                status,
                error,
                path,
                code,
                message,
                fieldErrors == null ? Collections.emptyList() : List.copyOf(fieldErrors)
        );
    }
}

