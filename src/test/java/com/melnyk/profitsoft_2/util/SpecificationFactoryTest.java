package com.melnyk.profitsoft_2.util;

import com.melnyk.profitsoft_2.dto.request.GenreFilter;
import com.melnyk.profitsoft_2.entity.Genre;
import jakarta.persistence.criteria.*;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SpecificationFactoryTest {

    @Test
    void create_withNullFilter_returnsNull() {
        Specification<Genre> spec = SpecificationFactory.create(null);
        assertThat(spec.toPredicate(null, null, null)).isNull();
    }

    @Test
    @SuppressWarnings("unchecked")
    void create_withNameFilter_returnsPredicate() {
        GenreFilter filter = new GenreFilter("Drama", null, null, null);

        Root<Genre> root = (Root<Genre>) mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Path<String> path = (Path<String>) mock(Path.class);
        Predicate predicate = mock(Predicate.class);

        when(root.<String>get("name")).thenReturn(path);
        when(cb.lower(path)).thenReturn(path);
        when(cb.like(path, "%drama%")).thenReturn(predicate);
        when(cb.and(any(Predicate[].class))).thenReturn(predicate);

        Specification<Genre> spec = SpecificationFactory.create(filter);
        Predicate result = spec.toPredicate(root, query, cb);

        assertThat(result).isEqualTo(predicate);

        verify(root).get("name");
        verify(cb).lower(path);
        verify(cb).like(path, "%drama%");
        verify(cb).and(predicate);
    }

}