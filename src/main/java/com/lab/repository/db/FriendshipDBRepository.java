package com.lab.repository.db;

import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.validator.Validator;

/**
 * Skeleton database repository implementation for {@link Friendship} entities
 * Extends {@link AbstractDBRepository} and prepares the environment for JDBC persistence
 */
public class FriendshipDBRepository extends AbstractDBRepository<Tuple<Long,Long> , Friendship> {
    public FriendshipDBRepository(Validator<Friendship> validator, String url, String user, String password) {
        super(validator, url, user, password);
    }
}
