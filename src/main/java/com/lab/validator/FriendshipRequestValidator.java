package com.lab.validator;

import com.lab.domain.FriendshipRequest;
import com.lab.exceptions.ValidationException;

import java.time.LocalDateTime;

public class FriendshipRequestValidator implements Validator<FriendshipRequest>
{
    @Override
    public void validate(FriendshipRequest entity) throws ValidationException {
        if (entity.getFromId() == null || entity.getToId() == null) {
            throw new ValidationException("Ids must not be null");
        }
        StringBuilder errors = new StringBuilder();
        if (entity.getToId().equals(entity.getFromId())) {
           errors.append("A user cannot befriend itself");
        }
        if(entity.getDate().isAfter(LocalDateTime.now())){
            errors.append("The date request must not be in the future");
        }
    }
}
