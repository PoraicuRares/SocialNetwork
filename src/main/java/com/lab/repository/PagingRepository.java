package com.lab.repository;

import com.lab.domain.Entity;
import com.lab.util.paging.Page;
import com.lab.util.paging.Pageable;

/**
 * Represents a repository that supports paging operations.
 * @param <ID> the type of the entity's unique identifier
 * @param <E>  the type of entities in the repository
 */
public interface PagingRepository<ID,E extends Entity<ID>> extends Repository<ID ,E> {
    /**
     * Retrieves a page of entities from the repository based on the provided pageable information.
     * @param pageable the paging information, including page number and page size
     * @return a Page object containing the entities on the requested page and the total number of entities
     */
   Page<E> findAllOnPage(Pageable pageable);
}

