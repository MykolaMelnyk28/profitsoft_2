package com.melnyk.profitsoft_2.util;

import com.melnyk.profitsoft_2.config.props.PaginationProps;
import com.melnyk.profitsoft_2.dto.request.filter.PageFilter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

public final class PageUtil {
    private PageUtil() {}

    public static Pageable pageableFrom(PageFilter filter, PaginationProps props) {
        Objects.requireNonNull(filter);
        int page = filter.page() != null ? filter.page() : props.getPage();
        int size = filter.size() != null ? filter.size() : props.getSize();
        Sort sort = parseSort(filter.sort(), props.getSort());
        return PageRequest.of(page, size, sort);
    }

    public static Sort parseSort(String sortParam, String defaultSort) {
        String[] parts;
        if (sortParam == null || sortParam.isBlank()) {
            parts = defaultSort.split(",");
        } else {
            parts = sortParam.split(",");
        }

        String property = parts[0].trim();

        Sort.Direction direction = Sort.Direction.ASC;

        if (parts.length > 1) {
            direction = Sort.Direction.fromString(parts[1].trim());
        }

        return Sort.by(new Sort.Order(direction, property));
    }

}
