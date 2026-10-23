package com.lab.exceptions;

/**
 * Exceptie aruncata atunci cand datele introduse pentru o entitate sunt invalide
 */
public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
