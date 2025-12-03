package com.melnyk.profitsoft_2.dto.request.filter.impl;

import com.melnyk.profitsoft_2.dto.request.filter.CreationFilter;
import com.melnyk.profitsoft_2.dto.request.filter.PageFilter;
import com.melnyk.profitsoft_2.dto.request.filter.UpdatedFilter;
import com.melnyk.profitsoft_2.entity.Book;
import com.melnyk.profitsoft_2.validaton.SortExpression;

import java.time.Instant;
import java.util.Set;

public record BookFilter(
    String title,
    Integer minYearPublished,
    Integer maxYearPublished,
    Set<Long> authorIds,
    Integer minPages,
    Integer maxPages,
    Set<Long> genreIds,
    Integer page,
    Integer size,
    @SortExpression(targetType = Book.class) String sort,
    Instant startCreatedAt,
    Instant endCreatedAt,
    Instant startUpdatedAt,
    Instant endUpdatedAt
) implements PageFilter, CreationFilter, UpdatedFilter { }
