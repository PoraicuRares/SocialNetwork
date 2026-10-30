package com.lab.domain;

import java.util.Set;
import java.util.function.Predicate;

/**
 * Reusable search criteria for {@link User} entities.
 * Each method returns a {@link Predicate} that can be passed to
 * {@code Repository.findAll(Predicate)} and combined with
 * {@code and}, {@code or}, {@code negate} or {@code Predicate.not}.
 */
public class UserCriteria {
    private UserCriteria(){

    }

    /**
     * Creates a criterion that accepts the users whose identifier belongs to the given set.
     *
     * @param ids the set of accepted identifiers
     * @return a predicate that is true for users whose id is contained in {@code ids}
     */
    public static Predicate<User> hasIdIn(Set<Long> ids){
        return u -> ids.contains(u.getId());
    }

    /**
     * Creates a criterion that accepts the users whose first name starts with the given prefix.
     *
     * @param prefix the expected beginning of the first name
     * @return a predicate that is true for users whose first name starts with {@code prefix}
     */
    public static Predicate<User> firstNameStartsWith(String prefix){
        return u -> u.getFirstName().startsWith(prefix);
    }

    /**
     * Creates a criterion that accepts the users with exactly the given last name.
     *
     * @param lastName the expected last name
     * @return a predicate that is true for users with this last name
     */
    public static Predicate<User> hasLastName(String lastName){
        return u -> u.getLastName().equals(lastName);
    }
}
