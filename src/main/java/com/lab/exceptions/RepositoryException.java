package com.lab.exceptions;

/**
 * Unchecked exception thrown when an error occurs during repository operations,
 * such as file IO errors, database connection issues, or missing entities
 */
public class RepositoryException extends RuntimeException {
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
