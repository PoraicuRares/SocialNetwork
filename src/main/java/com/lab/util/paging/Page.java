package com.lab.util.paging;

/**
 * Represents a page of results
 *
 * @param <E>                   the type of elements in the page
 * @param elementsOnPage        the elements on the current page
 * @param totalNumberOfElements the total number of elements across all pages
 */
public record Page<E>(Iterable<E> elementsOnPage, int totalNumberOfElements) {
}
