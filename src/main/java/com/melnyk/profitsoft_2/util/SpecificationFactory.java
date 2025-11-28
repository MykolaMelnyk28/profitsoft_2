package com.melnyk.profitsoft_2.util;

import com.melnyk.profitsoft_2.dto.request.filter.CreationFilter;
import com.melnyk.profitsoft_2.dto.request.filter.UpdatedFilter;
import com.melnyk.profitsoft_2.dto.request.filter.impl.AuthorFilter;
import com.melnyk.profitsoft_2.dto.request.filter.impl.GenreFilter;
import com.melnyk.profitsoft_2.entity.Author;
import com.melnyk.profitsoft_2.entity.Genre;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public final class SpecificationFactory {
    private SpecificationFactory() {}

    public static Specification<Genre> createForGenre(GenreFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return null;
            }
            final List<Predicate> predicates = new ArrayList<>();

            if (filter.name() != null) {
                predicates.add(useLikeIgnoreCase(root, cb, "name", "%" + filter.name() + "%"));
            }

            predicates.addAll(useCreationFilter(root, cb, filter));
            predicates.addAll(useUpdatedFilter(root, cb, filter));

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    public static Specification<Author> createForAuthor(AuthorFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return null;
            }
            final List<Predicate> predicates = new ArrayList<>();

            if (filter.firstName() != null) {
                predicates.add(useLikeIgnoreCase(root, cb, "firstName", "%" + filter.firstName() + "%"));
            }

            if (filter.lastName() != null) {
                predicates.add(useLikeIgnoreCase(root, cb, "lastName", "%" + filter.lastName() + "%"));
            }

            predicates.addAll(useCreationFilter(root, cb, filter));
            predicates.addAll(useUpdatedFilter(root, cb, filter));

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static Predicate useLikeIgnoreCase(Root<?> root, CriteriaBuilder cb, String name, String likeExpression) {
        return cb.like(cb.lower(root.get(name)), likeExpression.toLowerCase());
    }

    private static List<Predicate> useCreationFilter(Root<?> root, CriteriaBuilder cb, CreationFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter.startCreatedAt() != null) {
            LocalDateTime startCreatedAt = filter.startCreatedAt();
            Predicate predicate = cb.greaterThanOrEqualTo(root.get("createdAt"), startCreatedAt);
            predicates.add(predicate);
        }
        if (filter.endCreatedAt() != null) {
            LocalDateTime endCreatedAt = filter.endCreatedAt();
            Predicate predicate = cb.lessThanOrEqualTo(root.get("createdAt"), endCreatedAt);
            predicates.add(predicate);
        }
        return predicates;
    }

    private static List<Predicate> useUpdatedFilter(Root<?> root, CriteriaBuilder cb, UpdatedFilter filter) {
        List<Predicate> predicates = new ArrayList<>();
        if (filter.startUpdatedAt() != null) {
            LocalDateTime startUpdatedAt = filter.startUpdatedAt();
            Predicate predicate = cb.greaterThanOrEqualTo(root.get("updatedAt"), startUpdatedAt);
            predicates.add(predicate);
        }
        if (filter.endUpdatedAt() != null) {
            LocalDateTime endUpdatedAt = filter.endUpdatedAt();
            Predicate predicate = cb.lessThanOrEqualTo(root.get("updatedAt"), endUpdatedAt);
            predicates.add(predicate);
        }
        return predicates;
    }

}
