package com.melnyk.profitsoft_2.util;

import com.melnyk.profitsoft_2.dto.request.filter.impl.AuthorFilter;
import com.melnyk.profitsoft_2.dto.request.filter.impl.GenreFilter;
import com.melnyk.profitsoft_2.entity.Author;
import com.melnyk.profitsoft_2.entity.Genre;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SpecificationFactoryTest {

    @Test
    void createForGenre_withNullFilter_returnsNull() {
        Specification<Genre> spec = SpecificationFactory.createForGenre(null);
        assertThat(spec.toPredicate(null, null, null)).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void createForGenre_withNameFilter_returnsPredicate() {
        GenreFilter filter = new GenreFilter("Drama", null, null, null, null, null, null, null);

        Root<Genre> root = (Root<Genre>) mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate expected = mockStringLikePredicate(
            root, cb, "name", "%drama%"
        );

        Specification<Genre> spec = SpecificationFactory.createForGenre(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void createForGenre_withStartCreatedAt_returnsPredicate() {
        Instant time = Instant.now();
        GenreFilter filter = new GenreFilter(null, null, null, null, time, null, null, null);

        Specification<Genre> spec = SpecificationFactory.createForGenre(filter);
        Root<Genre> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        assertDatePredicate(spec, root, query, cb, "createdAt", time, true);
    }

    @Test
    void createForGenre_withEndCreatedAt_returnsPredicate() {
        Instant time = Instant.now();
        GenreFilter filter = new GenreFilter(null, null, null, null, null, time, null, null);

        Specification<Genre> spec = SpecificationFactory.createForGenre(filter);
        Root<Genre> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        assertDatePredicate(spec, root, query, cb, "createdAt", time, false);
    }

    @Test
    void createForGenre_withStartUpdatedAt_returnsPredicate() {
        Instant time = Instant.now();
        GenreFilter filter = new GenreFilter(null, null, null, null, null, null, time, null);

        Specification<Genre> spec = SpecificationFactory.createForGenre(filter);
        Root<Genre> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        assertDatePredicate(spec, root, query, cb, "updatedAt", time, true);
    }

    @Test
    void createForGenre_withEndUpdatedAt_returnsPredicate() {
        Instant time = Instant.now();
        GenreFilter filter = new GenreFilter(null, null, null, null, null, null, null, time);

        Specification<Genre> spec = SpecificationFactory.createForGenre(filter);
        Root<Genre> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        assertDatePredicate(spec, root, query, cb, "updatedAt", time, false);
    }

    // createForAuthor

    @Test
    void createForAuthor_withNullFilter_returnsNull() {
        Specification<Author> spec = SpecificationFactory.createForAuthor(null);
        assertThat(spec.toPredicate(null, null, null)).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void createForAuthor_withNameFilter_returnsPredicate() {
        AuthorFilter filter = new AuthorFilter("firstName1", "lastName1", null, null, null, null, null, null, null);

        Root<Author> root = (Root<Author>) mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        Predicate expectedFirstNamePred = mockStringLikePredicate(
            root, cb, "firstName", "%firstName1%"
        );
        Predicate expectedLastNamePred = mockStringLikePredicate(
            root, cb, "lastName", "%lastName1%"
        );
        Predicate expected = cb.and(expectedFirstNamePred, expectedLastNamePred);

        Specification<Author> spec = SpecificationFactory.createForAuthor(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void createForAuthor_withStartCreatedAt_returnsPredicate() {
        Instant time = Instant.now();
        AuthorFilter filter = new AuthorFilter(null, null, null, null, null, time, null, null, null);

        Specification<Author> spec = SpecificationFactory.createForAuthor(filter);
        Root<Author> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        assertDatePredicate(spec, root, query, cb, "createdAt", time, true);
    }

    @Test
    void createForAuthor_withEndCreatedAt_returnsPredicate() {
        Instant time = Instant.now();
        AuthorFilter filter = new AuthorFilter(null, null, null, null, null, null, time, null, null);

        Specification<Author> spec = SpecificationFactory.createForAuthor(filter);
        Root<Author> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        assertDatePredicate(spec, root, query, cb, "createdAt", time, false);
    }

    @Test
    void createForAuthor_withStartUpdatedAt_returnsPredicate() {
        Instant time = Instant.now();
        AuthorFilter filter = new AuthorFilter(null, null, null, null, null, null, null, time, null);

        Specification<Author> spec = SpecificationFactory.createForAuthor(filter);
        Root<Author> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        assertDatePredicate(spec, root, query, cb, "updatedAt", time, true);
    }

    @Test
    void createForAuthor_withEndUpdatedAt_returnsPredicate() {
        Instant time = Instant.now();
        AuthorFilter filter = new AuthorFilter(null, null, null, null, null, null, null, null, time);

        Specification<Author> spec = SpecificationFactory.createForAuthor(filter);
        Root<Author> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);

        assertDatePredicate(spec, root, query, cb, "updatedAt", time, false);
    }

    @SuppressWarnings("unchecked")
    Predicate mockStringLikePredicate(
        Root<?> root,
        CriteriaBuilder cb,
        String field,
        String likeExpression
    ) {
        Path<String> path = (Path<String>) mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.<String>get(field)).thenReturn(path);
        when(cb.lower(path)).thenReturn(path);
        when(cb.like(path, likeExpression)).thenReturn(predicate);
        when(cb.and(predicate)).thenReturn(predicate);

        return predicate;
    }

    @SuppressWarnings("unchecked")
    <T> void assertDatePredicate(
        Specification<T> specification,
        Root<T> root,
        CriteriaQuery<?> query,
        CriteriaBuilder cb,
        String fieldName,
        Instant expectedTime,
        boolean isStart
    ) {
        Path<Instant> path = (Path<Instant>) mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.<Instant>get(fieldName)).thenReturn(path);

        if (isStart) {
            when(cb.greaterThanOrEqualTo(path, expectedTime)).thenReturn(predicate);
        } else {
            when(cb.lessThanOrEqualTo(path, expectedTime)).thenReturn(predicate);
        }

        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        Predicate result = specification.toPredicate(root, query, cb);
        assertThat(result).isEqualTo(predicate);

        verify(root).get(fieldName);

        if (isStart) {
            verify(cb).greaterThanOrEqualTo(path, expectedTime);
        } else {
            verify(cb).lessThanOrEqualTo(path, expectedTime);
        }

        verify(cb).and(predicate);
    }

}