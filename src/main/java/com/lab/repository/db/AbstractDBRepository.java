package com.lab.repository.db;

import com.lab.domain.Entity;
import com.lab.repository.Repository;
import com.lab.validator.Validator;import java.util.Optional;

/**
 * Abstract base class for JDBC Database repositories
 * Stores connection credentials and throws {@link UnsupportedOperationException}
 * for all CRUD methods as preparation for the next laboratory requirement
 * @param <ID> the type of the entity's identifier
 * @param <E> the type of the entity, which must extend {@link Entity}
 */
public abstract class AbstractDBRepository<ID , E extends Entity<ID>> implements Repository<ID   , E> {
    protected final Validator<E> validator;
    protected final String url;
    protected final String user;
    protected final String password;

    public AbstractDBRepository(Validator<E> validator, String url, String user, String password) {
        this.validator = validator;
        this.url = url;
        this.user = user;
        this.password = password;
    }

    @Override
    public Iterable<E> findAll() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public Optional<E> findOne(ID id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void delete(ID id) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void save(E entity) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
