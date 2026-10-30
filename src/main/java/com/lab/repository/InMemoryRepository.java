package com.lab.repository;

import com.lab.domain.Entity;
import com.lab.exceptions.RepositoryException;
import com.lab.validator.Validator;

import java.util.HashMap;
import java.util.Map;import java.util.Optional;

/**
 * In-memory implementation of the Repository interface
 * Stores entities in a HashMap and provides basic CRUD operations
 * @param <ID> the type of the entity's identifier
 * @param <E> the type of the entity, which must extend Entity
 */
public class InMemoryRepository<ID , E extends Entity<ID>> implements Repository<ID , E>{
    private final Map<ID , E> entities = new HashMap<>();
    private final Validator<E> validator;

    public InMemoryRepository(Validator<E> validator) {
        this.validator = validator;
    }

    @Override
    public void save(E entity) {
        validator.validate(entity);
        if(entities.containsKey(entity.getId())){
            throw new RepositoryException("An entity with the same id already exists!");
        }
        entities.put(entity.getId() ,entity);
    }

    @Override
    public void delete(ID id) {
        if(!entities.containsKey(id)){
            throw new RepositoryException("An entity with this id does not exist!");
        }
        entities.remove(id);
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values().stream().toList();
    }

    @Override
    public Optional<E> findOne(ID id) {
        return Optional.ofNullable(entities.get(id));
    }
}
