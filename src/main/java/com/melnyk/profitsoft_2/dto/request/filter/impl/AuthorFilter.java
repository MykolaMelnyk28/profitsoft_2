package com.melnyk.profitsoft_2.dto.request.filter.impl;

import com.melnyk.profitsoft_2.dto.request.filter.CreationFilter;
import com.melnyk.profitsoft_2.dto.request.filter.PageFilter;
import com.melnyk.profitsoft_2.dto.request.filter.QueryFilter;
import com.melnyk.profitsoft_2.dto.request.filter.UpdatedFilter;
import com.melnyk.profitsoft_2.entity.Author;
import com.melnyk.profitsoft_2.validaton.SortExpression;

import java.time.Instant;

public record AuthorFilter(
    String query,
    String firstName,
    String lastName,
    Integer page,
    Integer size,
    @SortExpression(targetType = Author.class) String sort,
    Instant startCreatedAt,
    Instant endCreatedAt,
    Instant startUpdatedAt,
    Instant endUpdatedAt
) implements QueryFilter, PageFilter, CreationFilter, UpdatedFilter { }
