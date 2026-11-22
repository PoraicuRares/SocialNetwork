package com.lab.util.paging;


/**
 * Describes which page of a result is requested.
 *
 * @param pageNumber the index of the page, starting from 0
 * @param pageSize   the maximum number of elements on a page
 */
public record Pageable(int pageNumber, int pageSize) {

}
