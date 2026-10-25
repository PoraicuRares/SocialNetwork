package com.lab.exceptions;

/**
 * Unchecked exception thrown when an entity fails validation rules
 * before being saved or updated
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
