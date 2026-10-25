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
            throw new RepositoryException("The line must contain 2 attributes delimited by ';' ");
        Friendship friendship = new Friendship();
        friendship.setId(new Tuple<>(Long.parseLong(attributes[0])  , Long.parseLong(attributes[1]) ));
        friendship.setDate(LocalDateTime.parse(attributes[2]));
        return friendship;
    }

    @Override
    protected String createEntityAsString(Friendship entity) {
        return entity.getId().getLeft() + ";" + entity.getId().getRight() + ";" + entity.getDate();
    }
}
