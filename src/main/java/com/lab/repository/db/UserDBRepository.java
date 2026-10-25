package com.lab.repository.db;

import com.lab.domain.User;
import com.lab.validator.Validator;

/**
 * Skeleton database repository implementation for {@link User} entities
 * Extends {@link AbstractDBRepository} and prepares the environment for JDBC persistence
 */
public class UserDBRepository extends AbstractDBRepository<Long , User> {

    public UserDBRepository(Validator<User> validator, String url, String user, String password) {
        super(validator, url, user, password);
    }
}
