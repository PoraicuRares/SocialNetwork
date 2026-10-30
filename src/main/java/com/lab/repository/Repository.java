package com.lab.repository;


import com.lab.domain.Entity;
import com.lab.exceptions.RepositoryException;
import com.lab.exceptions.ValidationException;

import java.util.List;
import java.util.Optional;import java.util.function.Predicate;import java.util.stream.Collectors;import java.util.stream.Stream;
import java.util.stream.StreamSupport;


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
     * @return an {@link Optional} containing the found entity,
     * or an empty {@link Optional} if no entity has the given id
     */
    Optional<E> findOne(ID id);

    /**
     * Returns an iterable collection of all entities
     * @return an Iterable containing all entities
     */
    Iterable<E> findAll();

    /**
     * Returns a sequential stream over all entities.
     * This is the single place where the {@link Iterable} returned by {@link #findAll()}
     * is converted into a {@link Stream}.
     * @return a new stream of all entities; it can be consumed only once
     */
    default Stream<E> stream(){
        return StreamSupport.stream(findAll().spliterator() , false);
    }

    /**
     * Returns all entities that satisfy the given condition.
     * @param filter the condition an entity must satisfy to be included in the result
     * @return a list with the matching entities, possibly empty
     */
    default List<E> findAll(Predicate<E> filter){
        return stream()
                .filter(filter)
                .collect(Collectors.toList());
    }
}
