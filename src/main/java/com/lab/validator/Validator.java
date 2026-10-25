package com.lab.validator;


import com.lab.exceptions.ValidationException;

/**
 * Generic interface for entity validation, implementing the Strategy design pattern
 * @param <T> the type of the entity to be validated
 */
public interface Validator <T>{
    /**
     * Validates the specified entity.
     *
     * @param entity the entity to validate
     * @throws ValidationException if the entity attributes do not meet the validation rules
     */
    void validate(T entity) throws ValidationException;

}
