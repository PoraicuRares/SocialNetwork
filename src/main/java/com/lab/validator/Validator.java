package com.lab.validator;


import com.lab.exceptions.ValidationException;

/**
 * Generic interface for entity validation, implementing the Strategy design pattern
 * @param <T> the type of the entity to be validated
 */
@FunctionalInterface
public interface Validator <T>{
    /**
     * Validates the specified entity.
     *
     * @param entity the entity to validate
     * @throws ValidationException if the entity attributes do not meet the validation rules
     */
    void validate(T entity) throws ValidationException;

    /**
     * Composes this validator with another one. The resulting validator applies
     * this validator first and then {@code other}; the first rule that fails stops the validation.
     * @param other the validator to apply after this one
     * @return a validator that applies both rules in sequence
     */
    default Validator<T> andThen(Validator<T> other){
        return entity -> {
            this.validate(entity);
            other.validate(entity);
        };
    }
}
