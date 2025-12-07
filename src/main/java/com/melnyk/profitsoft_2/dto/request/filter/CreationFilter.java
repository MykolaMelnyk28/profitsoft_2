package com.melnyk.profitsoft_2.dto.request.filter;

import java.time.Instant;

/**
 * Filter interface for selecting entities by creation time range.
 */
public interface CreationFilter {

    /** Start of the creation timestamp range (inclusive). */
    Instant startCreatedAt();

    /** End of the creation timestamp range (inclusive). */
    Instant endCreatedAt();
}