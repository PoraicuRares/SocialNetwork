package com.lab.repository;

import java.util.Collection;
import java.util.Comparator;

/**
 * Identifier generation strategies for repositories that store their entities in memory.
 * The methods are meant to be passed to {@link InMemoryRepository} as method references.
 */
public final class IdGenerators {
    private IdGenerators() {

    }

    /**
     * Generates the next numeric identifier as the greatest existing one plus one
     *
     * @param existingIds the identifiers already in use
     * @return the next free identifier, 1 if there are no identifiers yet
     */
    public static Long nextLong(Collection<Long> existingIds) {
        return existingIds.stream()
                .max(Comparator.naturalOrder())
                .orElse(0L) + 1;
    }
}
