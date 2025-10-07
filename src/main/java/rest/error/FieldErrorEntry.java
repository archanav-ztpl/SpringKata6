package rest.error;

/**
 * Represents a single field validation error (field -> message).
 */
public record FieldErrorEntry(String field, String message) { }

