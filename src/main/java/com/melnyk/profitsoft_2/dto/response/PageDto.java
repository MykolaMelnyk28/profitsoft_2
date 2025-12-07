package com.melnyk.profitsoft_2.dto.response;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * The dto class that represents a pagination page
 */
public record PageDto<T extends InfoDto>(
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