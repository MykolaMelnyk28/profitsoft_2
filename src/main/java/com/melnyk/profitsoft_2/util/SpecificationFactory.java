package com.melnyk.profitsoft_2.util;

import com.melnyk.profitsoft_2.dto.request.filter.impl.BookFilter;
import com.melnyk.profitsoft_2.dto.request.filter.CreationFilter;
import com.melnyk.profitsoft_2.dto.request.filter.UpdatedFilter;
import com.melnyk.profitsoft_2.dto.request.filter.impl.AuthorFilter;
import com.melnyk.profitsoft_2.dto.request.filter.impl.GenreFilter;
import com.melnyk.profitsoft_2.entity.Author;
import com.melnyk.profitsoft_2.entity.Book;
import com.melnyk.profitsoft_2.entity.Genre;
import jakarta.persistence.criteria.*;
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

    public static Specification<Book> createForBook(BookFilter filter) {
        return (root, query, cb) -> {
            if (filter == null) {
                return cb.conjunction();
            }

            query.distinct(true);

            if (query.getResultType() != Long.class) {
                root.fetch("author", JoinType.LEFT);
                root.fetch("genres", JoinType.LEFT);
            }

            final List<Predicate> predicates = new ArrayList<>();

            if (filter.title() != null) {
                predicates.add(useLikeIgnoreCase(root, cb, "title", "%" + filter.title() + "%"));
            }

            if (filter.minYearPublished() != null) {
                predicates.add(useMinInt(root, cb, "yearPublished", filter.minYearPublished()));
            }

            if (filter.maxYearPublished() != null) {
                predicates.add(useMaxInt(root, cb, "yearPublished", filter.maxYearPublished()));
            }

            if (filter.minPages() != null) {
                predicates.add(useMinInt(root, cb, "pages", filter.minYearPublished()));
            }

            if (filter.maxPages() != null) {
                predicates.add(useMaxInt(root, cb, "pages", filter.maxYearPublished()));
            }

            if (filter.authorIds() != null && !filter.authorIds().isEmpty()) {
                predicates.add(root.get("author").get("id").in(filter.authorIds()));
            }

            if (filter.genreIds() != null && !filter.genreIds().isEmpty()) {
                Join<Book, Genre> genreJoin = root.join("genres", JoinType.LEFT);
                predicates.add(genreJoin.get("id").in(filter.genreIds()));
            }

            predicates.addAll(useCreationFilter(root, cb, filter));
            predicates.addAll(useUpdatedFilter(root, cb, filter));

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static Predicate useLikeIgnoreCase(Root<?> root, CriteriaBuilder cb, String name, String likeExpression) {
        return cb.like(cb.lower(root.get(name)), likeExpression.toLowerCase());
    }

    private static Predicate useMinInt(Root<?> root, CriteriaBuilder cb, String name, Integer value) {
        return cb.greaterThanOrEqualTo(root.get(name), value);
    }

    private static Predicate useMaxInt(Root<?> root, CriteriaBuilder cb, String name, Integer value) {
        return cb.lessThanOrEqualTo(root.get(name), value);
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
