package com.melnyk.profitsoft_2.dto.request;

public record GenreFilter(
    String name,
    Integer page,
    Integer size,
    String sort
) implements PageFilter { }
