package com.melnyk.profitsoft_2.dto.request.filter.impl;

import com.melnyk.profitsoft_2.dto.request.filter.CreationFilter;
import com.melnyk.profitsoft_2.dto.request.filter.PageFilter;
import com.melnyk.profitsoft_2.dto.request.filter.UpdatedFilter;
import com.melnyk.profitsoft_2.entity.Author;
import com.melnyk.profitsoft_2.validaton.SortExpression;

import java.time.LocalDateTime;

public record AuthorFilter(
    String firstName,
    String lastName,
    Integer page,
    Integer size,
    @SortExpression(targetType = Author.class) String sort,
    LocalDateTime startCreatedAt,
    LocalDateTime endCreatedAt,
    LocalDateTime startUpdatedAt,
    LocalDateTime endUpdatedAt
) implements PageFilter, CreationFilter, UpdatedFilter { }
