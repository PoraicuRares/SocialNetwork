package com.lab.exceptions;

/**
 * Unchecked exception thrown when attempting to perform operations
 * or retrieve data from an empty collection
 */
public class EmptyListException extends RuntimeException {
    public EmptyListException(String message) {
        super(message);
    }
}
