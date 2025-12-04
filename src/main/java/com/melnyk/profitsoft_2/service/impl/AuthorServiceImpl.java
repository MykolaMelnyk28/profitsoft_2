package com.melnyk.profitsoft_2.service.impl;

import com.melnyk.profitsoft_2.config.aspect.LogServiceMethod;
import com.melnyk.profitsoft_2.config.props.PaginationProps;
import com.melnyk.profitsoft_2.dto.request.AuthorRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.AuthorFilter;
import com.melnyk.profitsoft_2.dto.response.AuthorDetailsDto;
import com.melnyk.profitsoft_2.dto.response.AuthorInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.entity.Author;
import com.melnyk.profitsoft_2.exception.ResourceAlreadyExistsException;
import com.melnyk.profitsoft_2.exception.ResourceNotFoundException;
import com.melnyk.profitsoft_2.mapper.AuthorMapper;
import com.melnyk.profitsoft_2.repository.AuthorRepository;
import com.melnyk.profitsoft_2.service.AuthorService;
import com.melnyk.profitsoft_2.util.PageUtil;
import com.melnyk.profitsoft_2.util.SpecificationFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorMapper authorMapper;
    private final PaginationProps paginationProps;
    private final TransactionTemplate transactionTemplate;

    @Override
    @LogServiceMethod(logArgs = true)
    public AuthorDetailsDto create(AuthorRequestDto body) throws ResourceAlreadyExistsException {
        Author created = transactionTemplate.execute(status -> createAuthor(body));
        return authorMapper.toDetailsDto(created);
    }

    @Override
    @Transactional(readOnly = true)
    @LogServiceMethod(logArgs = true)
    public AuthorDetailsDto getById(Long id) throws ResourceNotFoundException {
        AuthorDetailsDto authorDetailsDto = authorMapper.toDetailsDto(getByIdOrThrow(id));
        return authorDetailsDto;
    }

    @Override
    @Transactional(readOnly = true)
    @LogServiceMethod(logArgs = true)
    public PageDto<AuthorInfoDto> search(AuthorFilter filter) {
        Pageable pageable = PageUtil.pageableFrom(filter, paginationProps);
        Specification<Author> spec = SpecificationFactory.createForAuthor(filter);
        Page<Author> page = authorRepository.findAll(spec, pageable);
        return new PageDto<>(page.map(authorMapper::toInfoDto));
    }

    @Override
    @LogServiceMethod(logArgs = true)
    public AuthorDetailsDto updateById(Long id, AuthorRequestDto body)
        throws ResourceNotFoundException, ResourceAlreadyExistsException {
        Author updated = transactionTemplate.execute(status -> updateAuthor(id, body));
        return authorMapper.toDetailsDto(updated);
    }

    @Override
    @Transactional
    @LogServiceMethod(logArgs = true)
    public void deleteById(Long id) throws ResourceNotFoundException {
        getByIdOrThrow(id);
        authorRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @LogServiceMethod(logArgs = true)
    public Author getByIdOrThrow(Long id) throws ResourceNotFoundException {
        return authorRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("%d not found".formatted(id), id, "Author"));
    }

    private Author createAuthor(AuthorRequestDto body) {
        Author author = authorMapper.toEntity(body);
        return authorRepository.save(author);
    }

    private Author updateAuthor(Long id, AuthorRequestDto body) {
        Author found = getByIdOrThrow(id);

        boolean isUpdated = false;

        if (!body.firstName().equals(found.getFirstName())) {
            found.setFirstName(body.firstName());
            isUpdated = true;
        }

        if (!body.lastName().equals(found.getLastName())) {
            found.setLastName(body.lastName());
            isUpdated = true;
        }

        if (isUpdated) {
            return authorRepository.save(found);
        }
        return found;
    }

}
