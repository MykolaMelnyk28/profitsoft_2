package com.melnyk.profitsoft_2.dto.request.filter.impl;

import com.melnyk.profitsoft_2.dto.request.filter.CreationFilter;
import com.melnyk.profitsoft_2.dto.request.filter.PageFilter;
import com.melnyk.profitsoft_2.dto.request.filter.UpdatedFilter;
import com.melnyk.profitsoft_2.entity.Genre;
import com.melnyk.profitsoft_2.validaton.SortExpression;

import java.time.Instant;

public record GenreFilter(
    String name,
    Integer page,
    Integer size,
    @SortExpression(targetType = Genre.class) String sort,
    Instant startCreatedAt,
    Instant endCreatedAt,
    Instant startUpdatedAt,
    Instant endUpdatedAt
) implements PageFilter, CreationFilter, UpdatedFilter { }
