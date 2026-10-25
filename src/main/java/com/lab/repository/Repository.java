package com.lab.repository;


import com.lab.domain.Entity;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ValidationException;


/**
 * Generic CRUD interface for managing entities
 * @param <ID> the type of the entity's identifier
 * @param <E> the type of the entity, which must extend {@link Entity}
 */
public interface Repository<ID , E extends Entity<ID>> {

    /**
     * Saves the given entity
     * @param entity the entity to be saved
     * @throws ValidationException if the entity is not valid
     * @throws RepositoryException if an entity with the same ID already exists
     */
    void save(E entity);

    /**
     * Removes the entity with the specified identifier
     * @param id the identifier of the entity to be removed
     * @throws RepositoryException if an entity with the given ID does not exist
     */
    void delete(ID id);

    /**
     * Finds and returns an entity based on its identifier
     * @param id the identifier of the entity to search for
     * @return the found entity, or null if it does not exist
     */
    E findOne(ID id);

    /**
     * Returns an iterable collection of all entities
     * @return an Iterable containing all entities
     */
    Iterable<E> findAll();
}
