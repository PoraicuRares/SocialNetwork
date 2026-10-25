package com.lab.validator;

import com.lab.domain.User;
import com.lab.exceptions.ValidationException;

/**
 * Validator implementation for User entities
 * Ensures that the user's ID is valid and that the names are properly formatted
 */
public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        StringBuilder errors = new StringBuilder();
        if(entity.getId()  <= 0) errors.append("Id must be greater than zero!");

        if(entity.getFirstName() == null ) errors.append("First name must not be null!");
        else if(entity.getFirstName().isBlank()) errors.append("First name must not be blank!");
        else if (Character.isLowerCase(entity.getFirstName().charAt(0))) errors.append("First name must start with uppercase!\n");

        if(entity.getLastName() == null ) errors.append("Last name must not be null!");
        else if(entity.getLastName().isBlank()) errors.append("Last name must not be blank!");
        else if (Character.isLowerCase(entity.getLastName().charAt(0))) errors.append("Last name must start with uppercase!\n");
        if(!errors.isEmpty()){
            throw new ValidationException(errors.toString());
        }
    }
}
