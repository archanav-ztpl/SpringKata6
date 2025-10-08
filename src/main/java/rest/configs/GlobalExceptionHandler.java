package rest.configs;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rest.exceptions.AppException;
import rest.error.ErrorResponse;
import rest.error.FieldErrorEntry;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest request) {
        logger.error("Validation exception: {}", ex.getMessage(), ex);
        List<FieldErrorEntry> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new FieldErrorEntry(f.getField(), f.getDefaultMessage()))
                .collect(Collectors.toList());
        ErrorResponse body = build(HttpStatus.BAD_REQUEST, request.getRequestURI(), "VALIDATION_FAILED", "Validation failed", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolations(ConstraintViolationException ex, HttpServletRequest request) {
        logger.error("Handling ConstraintViolationException with structured handler");
        List<FieldErrorEntry> fieldErrors = ex.getConstraintViolations().stream()
                .map(this::mapConstraintViolation)
                .collect(Collectors.toList());
        ErrorResponse body = build(HttpStatus.BAD_REQUEST, request.getRequestURI(), "CONSTRAINT_VIOLATION", "Constraint violations", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {
        logger.error("AppException occurred: {}", ex.getMessage(), ex);
        HttpStatus status = HttpStatus.resolve(ex.getStatus());
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;
        logger.info("Handling AppException code={} status={}", ex.getCode(), status.value());
        ErrorResponse body = build(status, request.getRequestURI(), ex.getCode(), ex.getMessage(), null);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnhandled(Exception ex, HttpServletRequest request) {
        logger.error("Unhandled exception: {}", ex.getMessage(), ex);
        ErrorResponse body = build(HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI(), "INTERNAL_ERROR", "Unexpected internal server error", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(TypeMismatchException ex, HttpServletRequest request) {
        logger.error("Type mismatch exception: {}", ex.getMessage(), ex);
        String message = String.format("Invalid value '%s'. Expected type: %s.",
                ex.getValue(), ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown");
        ErrorResponse body = build(HttpStatus.BAD_REQUEST, request.getRequestURI(), "TYPE_MISMATCH", message, null);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = "Invalid ID format: " + ex.getValue();
        ErrorResponse errorResponse = ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                request.getRequestURI(),
                "TYPE_MISMATCH",
                message,
                null
        );
        return ResponseEntity.badRequest().body(errorResponse);
    }

    private FieldErrorEntry mapConstraintViolation(ConstraintViolation<?> v) {
        String path = v.getPropertyPath() == null ? null : v.getPropertyPath().toString();
        if (path != null && path.contains(".")) {
            path = path.substring(path.lastIndexOf('.') + 1);
        }
        return new FieldErrorEntry(path, v.getMessage());
    }

    private ErrorResponse build(HttpStatus status, String requestUri, String code, String message, List<FieldErrorEntry> fieldErrors) {
        return ErrorResponse.of(status.value(), status.getReasonPhrase(), requestUri, code, message, fieldErrors);
    }
}
