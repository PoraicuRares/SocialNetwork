package com.lab.repository.db;

import com.lab.domain.Friendship;
import com.lab.domain.User;
import com.lab.repository.Repository;
import com.lab.validator.Validator;

public class UserDBRepository  implements Repository<Long, User> {
    private final Validator<Friendship> validator;
    private final String url;
    private final String user;
    private final String password;

    public UserDBRepository() {
    }

    @Override
    public void save(User entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(Long aLong) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public User findOne(Long aLong) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Iterable<User> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");

    }
}
