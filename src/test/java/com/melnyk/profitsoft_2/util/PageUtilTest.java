package com.melnyk.profitsoft_2.util;

import com.melnyk.profitsoft_2.config.props.PaginationProps;
import com.melnyk.profitsoft_2.dto.request.filter.PageFilter;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

class PageUtilTest {

    record PageFilterImpl(Integer page, Integer size, String sort) implements PageFilter { }

    final PaginationProps props = new PaginationProps(1, 20, "name,asc");

    @Test
    void pageableFrom_withFilterValues_returnsCorrectPageable() {
        PageFilter filter = new PageFilterImpl(2, 5, "id,desc");
        Pageable pageable = PageUtil.pageableFrom(filter, props);

        assertThat(pageable.getPageNumber()).isEqualTo(2);
        assertThat(pageable.getPageSize()).isEqualTo(5);
        assertThat(pageable.getSort().getOrderFor("id").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void pageableFrom_withNullFilter_usesDefaults() {
        PageFilter filter = new PageFilterImpl(null, null, null);
        Pageable pageable = PageUtil.pageableFrom(filter, props);

        assertThat(pageable.getPageNumber()).isEqualTo(props.getPage());
        assertThat(pageable.getPageSize()).isEqualTo(props.getSize());
        assertThat(pageable.getSort().getOrderFor("name").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void parseSort_withBlank_usesDefaultSort() {
        Sort sort = PageUtil.parseSort("", "createdAt,desc");
        assertThat(sort.getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void parseSort_withAscDesc_andSpaces() {
        Sort sortAsc = PageUtil.parseSort(" title , ASC ", "id,desc");
        assertThat(sortAsc.getOrderFor("title").getDirection()).isEqualTo(Sort.Direction.ASC);

        Sort sortDesc = PageUtil.parseSort("created , desc", "id,asc");
        assertThat(sortDesc.getOrderFor("created").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

}