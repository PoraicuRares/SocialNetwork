package com.lab.repository;

import com.lab.domain.Entity;
import com.lab.util.paging.Page;
import com.lab.util.paging.Pageable;
import com.lab.validator.Validator;

import java.util.*;
import java.util.function.Function;

/**
 * In-memory implementation of the Repository interface
 * Stores entities in a HashMap and provides basic CRUD operations
 *
 * @param <ID> the type of the entity's identifier
 * @param <E>  the type of the entity, which must extend Entity
 */
public class InMemoryRepository<ID, E extends Entity<ID>> implements PagingRepository<ID, E> {
    private final Map<ID, E> entities = new LinkedHashMap<>();
    private final Validator<E> validator;
    private final Function<Collection<ID>, ID> idGenerator;


    public InMemoryRepository(Validator<E> validator) {
        this(validator, null);
    }

    public InMemoryRepository(Validator<E> validator, Function<Collection<ID>, ID> idGenerator) {
        this.validator = validator;
        this.idGenerator = idGenerator;
    }


    @Override
    public Optional<E> save(E entity) {
        requireNonNull(entity, "The entity");
        validator.validate(entity);
        if (entity.getId() == null) {
            if (idGenerator == null) {
                throw new IllegalArgumentException("The entity must have an id");
            }
            entity.setId(idGenerator.apply(entities.keySet()));
        }
        return Optional.ofNullable(entities.putIfAbsent(entity.getId(), entity))
                .map(existing -> entity);
    }

    @Override
    public Optional<E> delete(ID id) {
        requireNonNull(id, "The id");
        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity) {
        requireNonNull(entity, "The entity");
        requireNonNull(entity.getId(), "The id");
        validator.validate(entity);
        return entities.replace(entity.getId(), entity) == null
                ? Optional.of(entity)
                : Optional.empty();
    }

    @Override
    public Iterable<E> findAll() {
        return entities.values().stream().toList();
    }

    @Override
    public Optional<E> findOne(ID id) {
        requireNonNull(id, "The id");
        return Optional.ofNullable(entities.get(id));
    }

    private static void requireNonNull(Object value, String what) {
        if (value == null) {
            throw new IllegalArgumentException(what + " must not be null");
        }
    }

    @Override
    public Page<E> findAllOnPage(Pageable pageable) {
        List<E> elementsOnPage = entities.values()
                .stream()
                .skip((long) pageable.pageSize() * pageable.pageNumber())
                .limit(pageable.pageSize())
                .toList();
        return new Page<>(elementsOnPage, entities.size());
    }

}
