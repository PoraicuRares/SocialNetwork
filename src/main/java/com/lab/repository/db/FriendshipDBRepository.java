package com.lab.repository.db;

import com.lab.domain.Friendship;
import com.lab.domain.Tuple;
import com.lab.repository.Repository;
import com.lab.validator.Validator;

public class FriendshipDBRepository implements Repository<Tuple<Long,Long> , Friendship> {
    private final Validator<Friendship> validator;
    private final String url;
    private final String user;
    private final String password;

    public FriendshipDBRepository(Validator<Friendship> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public void save(Friendship entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(Tuple<Long, Long> longLongTuple) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Friendship findOne(Tuple<Long, Long> longLongTuple) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Iterable<Friendship> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
