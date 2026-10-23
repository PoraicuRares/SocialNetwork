package com.lab.exceptions;

/**
 * Exceptie aruncata atunci cand apar erori la accesarea sau modificarea datelor din Repository
 */
public class RepositoryException extends RuntimeException {
    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
