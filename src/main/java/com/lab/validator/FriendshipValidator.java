package com.lab.validator;


import com.lab.domain.Friend;
import com.lab.exceptions.ValidationException;

public class FriendshipValidator implements Validator<Friend> {
    @Override
    public void validate(Friend entity) throws ValidationException {
        if(entity.getIdFriend1() == null || entity.getIdFriend2() == null){
            throw new ValidationException("Id-urile nu trebuie sa fie null !");
        }
        if(entity.getIdFriend1().equals(entity.getIdFriend2())){
            throw new ValidationException("Un utilizator nu se poate imprieteni cu el!");
        }

    }
}
