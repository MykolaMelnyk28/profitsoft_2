package com.melnyk.profitsoft_2.dto.request.filter;

import java.time.Instant;

/**
 * Filter interface for selecting entities by last update time range.
 */
public interface UpdatedFilter {

    /** Start of the last update timestamp range (inclusive). */
    Instant startUpdatedAt();

    /** End of the last update timestamp range (inclusive). */
    Instant endUpdatedAt();
}