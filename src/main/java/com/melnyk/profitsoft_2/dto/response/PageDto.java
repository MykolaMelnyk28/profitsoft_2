package com.melnyk.profitsoft_2.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageDto<T>(
    List<T> content,
    int page,
    int size,
    long totalElements,
    int totalPages
) {

    public PageDto(Page<T> page) {
        this(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

}