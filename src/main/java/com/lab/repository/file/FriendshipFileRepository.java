package com.lab.repository.file;

import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.exceptions.RepositoryException;
import com.lab.validator.Validator;

import java.time.LocalDateTime;

/**
 * File-based repository implementation for {@link Friendship} entities
 * Extends {@link AbstractFileRepository} to handle specific serialization and deserialization of friendships
 */
public class FriendshipFileRepository extends AbstractFileRepository<Tuple<Long,Long>, Friendship> {
    public FriendshipFileRepository(Validator<Friendship> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    protected Friendship extractEntity(String line) {
        String[] attributes = line.split(";" , -1);
        if(attributes.length != 3)
            throw new RepositoryException("The line must contain 3 attributes delimited by ';' ");
        Friendship friendship = new Friendship();

        Long left = Long.parseLong(attributes[0].trim()) ;
        Long right = Long.parseLong(attributes[1].trim());

        if(left.compareTo(right) > 0){
            Long temp = left;
            left = right;
            right = temp;
        }
        friendship.setId(new Tuple<>(left , right));
        friendship.setDate(LocalDateTime.parse(attributes[2].trim()));
        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId().getLeft() + ";" + entity.getId().getRight() + ";" + entity.getDate();
    }
}
