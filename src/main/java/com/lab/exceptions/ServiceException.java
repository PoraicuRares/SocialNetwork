package com.lab.exceptions;

/**
 * Unchecked exception thrown when a logical or business error occurs
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
