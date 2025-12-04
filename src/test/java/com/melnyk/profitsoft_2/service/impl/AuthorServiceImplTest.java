package com.melnyk.profitsoft_2.service.impl;

import com.melnyk.profitsoft_2.config.props.PaginationProps;
import com.melnyk.profitsoft_2.dto.request.AuthorRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.PageFilter;
import com.melnyk.profitsoft_2.dto.request.filter.impl.AuthorFilter;
import com.melnyk.profitsoft_2.dto.response.AuthorDetailsDto;
import com.melnyk.profitsoft_2.dto.response.AuthorInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.entity.Author;
import com.melnyk.profitsoft_2.exception.ResourceNotFoundException;
import com.melnyk.profitsoft_2.mapper.AuthorMapper;
import com.melnyk.profitsoft_2.repository.AuthorRepository;
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

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @Mock
    AuthorRepository authorRepository;

    @Mock
    AuthorMapper authorMapper;

    @Mock
    TransactionTemplate transactionTemplate;

    @Mock
    PaginationProps paginationProps;

    @InjectMocks
    AuthorServiceImpl authorService;

    final Instant FIXED_CREATED_AT = Instant.now();

    @Test
    void create_whenValidRequest_thenReturnsCreatedDto() {
        AuthorRequestDto req = new AuthorRequestDto("createdFirstName", "createdLastName");

        Author entity = Author.builder()
            .id(1L)
            .firstName("createdFirstName")
            .lastName("createdLastName")
            .createdAt(FIXED_CREATED_AT)
            .updatedAt(FIXED_CREATED_AT)
            .build();

        AuthorDetailsDto dto = new AuthorDetailsDto(1L, "createdFirstName", "createdLastName", FIXED_CREATED_AT, FIXED_CREATED_AT);

        when(transactionTemplate.execute(any()))
            .thenAnswer(invocation -> {
                TransactionCallback<?> cb = invocation.getArgument(0);
                return cb.doInTransaction(null);
            });
        when(authorMapper.toEntity(req)).thenReturn(entity);
        when(authorRepository.save(entity)).thenReturn(entity);
        when(authorMapper.toDetailsDto(entity)).thenReturn(dto);

        AuthorDetailsDto result = authorService.create(req);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void create_whenNameAlreadyExists_thenReturnsCreatedDto() {
        AuthorRequestDto req = new AuthorRequestDto("existingFirstName", "existingLastName");

        Author entity = Author.builder()
            .id(10L)
            .firstName("existingFirstName")
            .lastName("existingLastName")
            .build();

        AuthorDetailsDto dto = new AuthorDetailsDto(1L, "createdFirstName", "createdLastName", FIXED_CREATED_AT, FIXED_CREATED_AT);

        when(transactionTemplate.execute(any()))
            .thenAnswer(invocation -> {
                TransactionCallback<?> cb = invocation.getArgument(0);
                return cb.doInTransaction(null);
            });
        when(authorMapper.toEntity(req)).thenReturn(entity);
        when(authorRepository.save(entity)).thenReturn(entity);
        when(authorMapper.toDetailsDto(entity)).thenReturn(dto);

        AuthorDetailsDto result = authorService.create(req);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getById_whenEntityExists_thenReturnsDto() {
        Author entity = Author.builder()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .createdAt(FIXED_CREATED_AT)
            .updatedAt(FIXED_CREATED_AT)
            .build();

        AuthorDetailsDto dto = new AuthorDetailsDto(1L, "firstName1", "lastName1", FIXED_CREATED_AT, FIXED_CREATED_AT);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(authorMapper.toDetailsDto(entity)).thenReturn(dto);

        AuthorDetailsDto result = authorService.getById(1L);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getById_whenNotFound_thenThrowsNotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.getById(1L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void search_whenFilterIsValid_thenReturnsPagedResult() {
        Pageable pageable = PageRequest.of(0, 10);
        AuthorFilter filter = new AuthorFilter(null, null, 0, 10, null, null, null, null, null);
        Author entity = Author.builder()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .createdAt(FIXED_CREATED_AT)
            .updatedAt(FIXED_CREATED_AT)
            .build();

        AuthorInfoDto dto = new AuthorInfoDto(1L, "firstName1", "lastName1");

        Page<Author> page = new PageImpl<>(List.of(entity));

        when(authorRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);

        when(authorMapper.toInfoDto(entity)).thenReturn(dto);

        try(var pageUtil = mockStatic(PageUtil.class)) {
            pageUtil.when(() -> PageUtil.pageableFrom(any(PageFilter.class), any(PaginationProps.class)))
                .thenReturn(pageable);

            PageDto<AuthorInfoDto> result = authorService.search(filter);

            assertThat(result.content()).containsExactly(dto);
            assertThat(result.totalElements()).isEqualTo(1);
        }
    }

    @Test
    void updateById_whenValidRequest_thenReturnsUpdatedDto() {
        AuthorRequestDto req = new AuthorRequestDto("updatedFirstName", "updatedLastName");

        Author found = Author.builder()
            .id(1L)
            .firstName("oldFirstName")
            .lastName("oldFirstName")
            .createdAt(FIXED_CREATED_AT).updatedAt(FIXED_CREATED_AT)
            .build();

        Author updated = Author.builder()
            .id(1L)
            .firstName("updatedFirstName")
            .lastName("updatedLastName")
            .createdAt(FIXED_CREATED_AT).updatedAt(FIXED_CREATED_AT)
            .build();

        AuthorDetailsDto dto = new AuthorDetailsDto(1L, "updatedFirstName", "updatedLastName", FIXED_CREATED_AT, FIXED_CREATED_AT);

        when(transactionTemplate.execute(any()))
            .thenAnswer(invocation -> {
                TransactionCallback<?> cb = invocation.getArgument(0);
                return cb.doInTransaction(null);
            });
        when(authorRepository.findById(1L)).thenReturn(Optional.of(found));
        when(authorRepository.save(found)).thenReturn(updated);
        when(authorMapper.toDetailsDto(updated)).thenReturn(dto);

        AuthorDetailsDto result = authorService.updateById(1L, req);

        assertThat(result).isEqualTo(dto);
    }

    @Test
    void updateById_whenNoChanges_thenReturnsSameDto() {
        AuthorRequestDto req = new AuthorRequestDto("firstName1", "lastName1");

        Author found = Author.builder()
            .id(1L)
            .firstName("firstName1")
            .lastName("lastName1")
            .createdAt(FIXED_CREATED_AT).updatedAt(FIXED_CREATED_AT)
            .build();

        AuthorDetailsDto dto = new AuthorDetailsDto(1L, "firstName1", "lastName1", FIXED_CREATED_AT, FIXED_CREATED_AT);

        when(transactionTemplate.execute(any()))
            .thenAnswer(invocation -> {
                TransactionCallback<?> cb = invocation.getArgument(0);
                return cb.doInTransaction(null);
            });
        when(authorRepository.findById(1L)).thenReturn(Optional.of(found));
        when(authorMapper.toDetailsDto(found)).thenReturn(dto);

        AuthorDetailsDto result = authorService.updateById(1L, req);

        assertThat(result).isEqualTo(dto);
        verify(authorRepository, never()).save(any());
    }

    @Test
    void deleteById_whenEntityExists_thenDeletesSuccessfully() {
        Author found = Author.builder()
            .id(1L)
            .firstName("firstName")
            .lastName("lastName")
            .build();

        when(authorRepository.findById(1L)).thenReturn(Optional.of(found));

        authorService.deleteById(1L);

        verify(authorRepository).deleteById(1L);
    }

    @Test
    void deleteById_whenNotFound_thenThrowsNotFound() {
        when(authorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authorService.deleteById(1L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

}