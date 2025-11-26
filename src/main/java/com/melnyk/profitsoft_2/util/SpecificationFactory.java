package com.melnyk.profitsoft_2.util;

import com.melnyk.profitsoft_2.dto.request.GenreFilter;
import com.melnyk.profitsoft_2.entity.Genre;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class SpecificationFactory {
    private SpecificationFactory() {}

    public static Specification<Genre> create(GenreFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return null;
            }
            final List<Predicate> predicates = new ArrayList<>();

            if (filter.name() != null) {
                String name = filter.name().toLowerCase();
                Predicate predicate = cb.like(cb.lower(root.get("name")), "%" + name + "%");
                predicates.add(predicate);
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

}
