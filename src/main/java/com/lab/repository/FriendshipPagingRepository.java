package com.lab.repository;

import com.lab.domain.User;
import com.lab.exceptions.RepositoryException;
import com.lab.util.paging.Page;
import com.lab.util.paging.Pageable;

/**
 * A repository that can return the friends of a user one page at a time.
 */
public interface FriendshipPagingRepository {
    /**
     * Returns one page of the users who are friends with the given user, sorted by id.
     *
     * @param userId   the id of the user whose friends are listed
     * @param pageable the page number and the page size
     * @return the friends on the page and the total number of friends; an empty page if it is after the last one
     * @throws RepositoryException if the storage cannot be accessed
     */
    Page<User> findFriendsOnPage(Long userId, Pageable pageable);
}
