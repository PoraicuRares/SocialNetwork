package com.lab.exceptions;

/**
 * Exceptie aruncata atunci cand se incearca operarea pe o lista sau o retea complet goala
 */
public class EmptyListException extends RuntimeException {
    public EmptyListException(String message) {
        super(message);
    }
}
