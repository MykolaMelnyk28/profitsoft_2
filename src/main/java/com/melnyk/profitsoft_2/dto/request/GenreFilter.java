package com.melnyk.profitsoft_2.dto.request;

import com.melnyk.profitsoft_2.entity.Genre;
import com.melnyk.profitsoft_2.validaton.SortExpression;

public record GenreFilter(
    String name,
    Integer page,
    Integer size,
    @SortExpression(targetType = Genre.class) String sort
) implements PageFilter { }
