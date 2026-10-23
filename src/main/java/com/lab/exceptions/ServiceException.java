package com.lab.exceptions;

/**
 * Exceptie aruncata atunci cand apar erori logice la nivelul stratului de Service
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
