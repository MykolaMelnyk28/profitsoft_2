package com.melnyk.profitsoft_2.service.impl;

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
    public AuthorDetailsDto create(AuthorRequestDto body) throws ResourceAlreadyExistsException {
        log.info("Creating author {}", body);

        Author created = transactionTemplate.execute(status -> createAuthor(body));

        log.info("Author created id={}", created.getId());
        return authorMapper.toDetailsDto(created);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthorDetailsDto getById(Long id) throws ResourceNotFoundException {
        log.info("Getting author id={}", id);
        AuthorDetailsDto authorDetailsDto = authorMapper.toDetailsDto(getByIdOrThrow(id));
        log.info("Author found id={}", id);
        return authorDetailsDto;
    }

    @Override
    @Transactional(readOnly = true)
    public PageDto<AuthorInfoDto> search(AuthorFilter filter) {
        log.info("Searching authors by filter {}", filter);
        Pageable pageable = PageUtil.pageableFrom(filter, paginationProps);
        Specification<Author> spec = SpecificationFactory.createForAuthor(filter);
        Page<Author> page = authorRepository.findAll(spec, pageable);
        log.info("{}", page);
        return new PageDto<>(page.map(authorMapper::toInfoDto));
    }

    @Override
    public AuthorDetailsDto updateById(Long id, AuthorRequestDto body)
        throws ResourceNotFoundException, ResourceAlreadyExistsException {
        log.info("Updating author id={}", id);

        Author updated = transactionTemplate.execute(status -> updateAuthor(id, body));

        log.info("Updated author id={}", id);
        return authorMapper.toDetailsDto(updated);
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws ResourceNotFoundException {
        log.info("Deleting author id={}", id);
        getByIdOrThrow(id);
        authorRepository.deleteById(id);
        log.info("Deleted author id={}", id);
    }

    @Transactional(readOnly = true)
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
