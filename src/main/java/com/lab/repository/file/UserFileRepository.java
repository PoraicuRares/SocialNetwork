package com.lab.repository.file;

import com.lab.domain.User;
import com.lab.exceptions.RepositoryException;
import com.lab.validator.Validator;

/**
 * File-based repository implementation for User entities
 * Extends {@link AbstractFileRepository} to handle specific serialization and deserialization of users
 */
public class UserFileRepository extends AbstractFileRepository<Long, User> {
    public UserFileRepository(Validator<User> validator, String fileName) {
        super(validator, fileName);
    }

    @Override
    protected User extractEntity(String line) {
        String[] attributes = line.split(";" ,-1);
        if(attributes.length != 3)
            throw new RepositoryException("The line must contain 3 attributes delimited by ';'");
        User user = new User();
        user.setId(Long.parseLong(attributes[0].trim()));
        user.setFirstName(attributes[1].trim());
        user.setLastName(attributes[2].trim());
        return user;
    }

    @Override
    protected String createEntityAsString(User entity) {
        return entity.getId() + ";" + entity.getFirstName() + ";" + entity.getLastName();
    }
}
