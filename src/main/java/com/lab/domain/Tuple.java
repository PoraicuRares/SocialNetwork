package com.lab.domain;

import java.util.Objects;

/**
 * A generic pair of two elements, used primarily to represent composite identifiers
 * @param <E1> the type of the first element
 * @param <E2> the type of the second element
 */
public class Tuple<E1 ,E2> {
    private final E1 left;
    private final E2 right;

    public Tuple(E1 left, E2 right) {
        this.left = left;
        this.right = right;
    }


    public E1 getLeft() {
        return left;
    }

    public E2 getRight() {
        return right;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Tuple<?, ?> tuple)) return false;
        return Objects.equals(left, tuple.left) && Objects.equals(right, tuple.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
