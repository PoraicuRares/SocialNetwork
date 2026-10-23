package com.lab.validator;

import com.lab.domain.User;
import com.lab.exceptions.ValidationException;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        StringBuilder errors = new StringBuilder();
        if(entity.getAge() == null || entity.getAge() < 0 ||  entity.getAge() > 150) errors.append("Varsta invalida!\n");
        if(entity.getFirstName() == null || entity.getFirstName().isBlank()) errors.append("Prenume invalid!\n");
        if(entity.getLastName() == null || entity.getLastName().isBlank()) errors.append("Nume de familie invalid!\n");
        if(!errors.isEmpty()){
            throw new ValidationException(errors.toString());
        }
    }
}
