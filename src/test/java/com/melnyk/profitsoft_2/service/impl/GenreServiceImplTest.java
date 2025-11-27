package com.melnyk.profitsoft_2.service.impl;

import com.melnyk.profitsoft_2.config.props.PaginationProps;
import com.melnyk.profitsoft_2.dto.request.GenreFilter;
import com.melnyk.profitsoft_2.dto.request.GenreRequestDto;
import com.melnyk.profitsoft_2.dto.request.PageFilter;
import com.melnyk.profitsoft_2.dto.response.GenreDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.entity.Genre;
import com.melnyk.profitsoft_2.exception.ResourceAlreadyExistsException;
import com.melnyk.profitsoft_2.exception.ResourceNotFoundException;
import com.melnyk.profitsoft_2.mapper.GenreMapper;
import com.melnyk.profitsoft_2.repository.GenreRepository;
import com.melnyk.profitsoft_2.util.PageUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceImplTest {

    @Mock
    GenreRepository genreRepository;

    @Mock
    GenreMapper genreMapper;

    @Mock
    TransactionTemplate transactionTemplate;

    @Mock
    PaginationProps paginationProps;

    @InjectMocks
    GenreServiceImpl genreService;

    final LocalDateTime FIXED_CREATED_AT = LocalDateTime.now();

    @Test
    void create_whenValidRequest_thenReturnsCreatedDto() {
        GenreRequestDto req = new GenreRequestDto("Drama");

        Genre entity = Genre.builder()
            .id(1L)
            .name("Drama")
            .createdAt(FIXED_CREATED_AT)
            .updatedAt(FIXED_CREATED_AT)
            .build();

        GenreDto dto = new GenreDto(1L, "Drama", FIXED_CREATED_AT, FIXED_CREATED_AT);

        when(transactionTemplate.execute(any()))
            .thenAnswer(invocation -> {
                TransactionCallback<?> cb = invocation.getArgument(0);
                return cb.doInTransaction(null);
            });
        when(genreRepository.findByName("Drama")).thenReturn(Optional.empty());
        when(genreMapper.toEntity(req)).thenReturn(entity);
        when(genreRepository.save(entity)).thenReturn(entity);
        when(genreMapper.toDto(entity)).thenReturn(dto);

        GenreDto result = genreService.create(req);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void create_whenNameAlreadyExists_thenThrowsConflict() {
        GenreRequestDto req = new GenreRequestDto("Drama");

        Genre existing = Genre.builder()
            .id(10L)
            .name("Drama")
            .build();

        when(transactionTemplate.execute(any()))
            .thenAnswer(invocation -> {
                TransactionCallback<?> cb = invocation.getArgument(0);
                return cb.doInTransaction(null);
            });
        when(genreRepository.findByName("Drama")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> genreService.create(req))
            .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void getById_whenEntityExists_thenReturnsDto() {
        Genre entity = Genre.builder()
            .id(1L)
            .name("Drama")
            .createdAt(FIXED_CREATED_AT)
            .updatedAt(FIXED_CREATED_AT)
            .build();

        GenreDto dto = new GenreDto(1L, "Drama", FIXED_CREATED_AT, FIXED_CREATED_AT);

        when(genreRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(genreMapper.toDto(entity)).thenReturn(dto);

        GenreDto result = genreService.getById(1L);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getById_whenNotFound_thenThrowsNotFound() {
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> genreService.getById(1L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void search_whenFilterIsValid_thenReturnsPagedResult() {
        Pageable pageable = PageRequest.of(0, 10);
        GenreFilter filter = new GenreFilter(null, 0, 10, null);
        Genre entity = Genre.builder()
            .id(1L)
            .name("Sci-Fi")
            .createdAt(FIXED_CREATED_AT)
            .updatedAt(FIXED_CREATED_AT)
            .build();

        GenreDto dto = new GenreDto(1L, "Sci-Fi", FIXED_CREATED_AT, FIXED_CREATED_AT);

        Page<Genre> page = new PageImpl<>(List.of(entity));

        when(genreRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);

        when(genreMapper.toDto(entity)).thenReturn(dto);

        try(var pageUtil = mockStatic(PageUtil.class)) {
            pageUtil.when(() -> PageUtil.pageableFrom(any(PageFilter.class), any(PaginationProps.class)))
                .thenReturn(pageable);

            PageDto<GenreDto> result = genreService.search(filter);

            assertThat(result.content()).containsExactly(dto);
            assertThat(result.totalElements()).isEqualTo(1);
        }
    }

    @Test
    void updateById_whenValidRequest_thenReturnsUpdatedDto() {
        GenreRequestDto req = new GenreRequestDto("New");

        Genre found = Genre.builder()
            .id(1L).name("Old")
            .createdAt(FIXED_CREATED_AT).updatedAt(FIXED_CREATED_AT)
            .build();

        Genre updated = Genre.builder()
            .id(1L).name("New")
            .createdAt(FIXED_CREATED_AT).updatedAt(FIXED_CREATED_AT)
            .build();

        GenreDto dto = new GenreDto(1L, "New", FIXED_CREATED_AT, FIXED_CREATED_AT);

        when(transactionTemplate.execute(any()))
            .thenAnswer(invocation -> {
                TransactionCallback<?> cb = invocation.getArgument(0);
                return cb.doInTransaction(null);
            });
        when(genreRepository.findById(1L)).thenReturn(Optional.of(found));
        when(genreRepository.findByName("New")).thenReturn(Optional.empty());
        when(genreRepository.save(found)).thenReturn(updated);
        when(genreMapper.toDto(updated)).thenReturn(dto);

        GenreDto result = genreService.updateById(1L, req);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void updateById_whenNameConflict_thenThrowsConflict() {
        GenreRequestDto req = new GenreRequestDto("Drama");

        Genre found = Genre.builder()
            .id(1L).name("Old")
            .build();

        Genre existing = Genre.builder()
            .id(2L).name("Drama")
            .build();

        when(transactionTemplate.execute(any()))
            .thenAnswer(invocation -> {
                TransactionCallback<?> cb = invocation.getArgument(0);
                return cb.doInTransaction(null);
            });
        when(genreRepository.findById(1L)).thenReturn(Optional.of(found));
        when(genreRepository.findByName("Drama")).thenReturn(Optional.of(existing));

        assertThatThrownBy(() -> genreService.updateById(1L, req))
            .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test
    void updateById_whenNoChanges_thenReturnsSameDto() {
        GenreRequestDto req = new GenreRequestDto("Same");

        Genre found = Genre.builder()
            .id(1L).name("Same")
            .createdAt(FIXED_CREATED_AT).updatedAt(FIXED_CREATED_AT)
            .build();

        GenreDto dto = new GenreDto(1L, "Same", FIXED_CREATED_AT, FIXED_CREATED_AT);

        when(transactionTemplate.execute(any()))
            .thenAnswer(invocation -> {
                TransactionCallback<?> cb = invocation.getArgument(0);
                return cb.doInTransaction(null);
            });
        when(genreRepository.findById(1L)).thenReturn(Optional.of(found));
        when(genreMapper.toDto(found)).thenReturn(dto);

        GenreDto result = genreService.updateById(1L, req);

        assertThat(result).isEqualTo(dto);
        verify(genreRepository, never()).save(any());
    }

    @Test
    void deleteById_whenEntityExists_thenDeletesSuccessfully() {
        Genre found = Genre.builder()
            .id(1L)
            .name("Drama")
            .build();

        when(genreRepository.findById(1L)).thenReturn(Optional.of(found));

        genreService.deleteById(1L);

        verify(genreRepository).deleteById(1L);
    }

    @Test
    void deleteById_whenNotFound_thenThrowsNotFound() {
        when(genreRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> genreService.deleteById(1L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

}