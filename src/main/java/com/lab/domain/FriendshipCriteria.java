package com.lab.domain;

import java.util.function.Predicate;

/**
 * Reusable search criteria for {@link Friendship} entities.
 * Each method returns a {@link Predicate} that can be passed to
 * {@code Repository.findAll(Predicate)} and combined with
 * {@code and}, {@code or}, {@code negate} or {@code Predicate.not}.
 */
public class FriendshipCriteria {
    private FriendshipCriteria(){

    }

    /**
     * Creates a criterion that accepts the friendships in which the given user takes part
     * @param userId the identifier of the user
     * @return a predicate that is true for friendships having {@code userId} on either side
     */
    public static Predicate<Friendship> involves(Long userId){
        return f -> f.getId().getLeft().equals(userId) || f.getId().getRight().equals(userId);
    }
}
