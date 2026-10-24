package com.lab.validator;


import com.lab.domain.Friendship;
import com.lab.exceptions.ValidationException;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getId().getLeft() == null || entity.getId().getRight() == null){
            throw new ValidationException("Ids must not be null!");
        }
        if(entity.getId().getLeft().equals(entity.getId().getRight())){
            throw new ValidationException("A user cannot befriend itself!");
        }

    }
}
