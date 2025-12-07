package com.melnyk.profitsoft_2.util;

import com.melnyk.profitsoft_2.config.props.PaginationProps;
import com.melnyk.profitsoft_2.dto.request.filter.PageFilter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Objects;

/**
 * Utility class for working with Pageable.
 *
 * <p>All methods are static and this class should not be instantiated.
 */
public final class PageUtil {
    private PageUtil() {}

    /**
     * Creates {@link Pageable} instance from filter, if some field in filter instance is null
     * then the default value will be taken from props.
     *
     * @param filter {@link PageFilter} instance given {@code page}, {@code size}, {@code sort} properties
     * @param props {@link PaginationProps} instance given the default values for {@code page}, {@code size},
     * {@code sort} properties
     * @return {@link Pageable} instance
     */
    public static Pageable pageableFrom(PageFilter filter, PaginationProps props) {
        Objects.requireNonNull(filter);
        int page = filter.page() != null ? filter.page() : props.getPage();
        int size = filter.size() != null ? filter.size() : props.getSize();
        Sort sort = parseSort(filter.sort(), props.getSort());
        return PageRequest.of(page, size, sort);
    }

    /**
     * Parse the {@link Sort} object from sort expression, if the parsed object is unsorted than will be used
     * defaultSort sort expression.
     * @param sortParam string with sort expression (nullable)
     * @param defaultSort string with default sort expression (required)
     * @return {@link Sort} instance
     */
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
