package com.melnyk.profitsoft_2.dto.request.filter;

/**
 * Filter interface for pagination and sorting parameters in API requests.
 */
public interface PageFilter {

    /** Page number (0-based). */
    Integer page();

    /** Number of items per page. */
    Integer size();

    /** Sorting criteria, e.g., "id,asc" or "name,desc". */
    String sort();

}
