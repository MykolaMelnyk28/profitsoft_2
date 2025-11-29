package com.melnyk.profitsoft_2.service.impl;

import com.melnyk.profitsoft_2.config.props.PaginationProps;
import com.melnyk.profitsoft_2.dto.request.GenreRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.GenreFilter;
import com.melnyk.profitsoft_2.dto.response.GenreDetailsDto;
import com.melnyk.profitsoft_2.dto.response.GenreInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.entity.Genre;
import com.melnyk.profitsoft_2.exception.ResourceAlreadyExistsException;
import com.melnyk.profitsoft_2.exception.ResourceNotFoundException;
import com.melnyk.profitsoft_2.mapper.GenreMapper;
import com.melnyk.profitsoft_2.repository.GenreRepository;
import com.melnyk.profitsoft_2.service.GenreService;
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

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private final TransactionTemplate transactionTemplate;
    private final PaginationProps paginationProps;

    @Override
    public GenreDetailsDto create(GenreRequestDto body) throws ResourceAlreadyExistsException {
        log.info("Creating genre {}", body);

        Genre created = transactionTemplate.execute(status -> createGenre(body));

        log.info("Genre created id={}", created.getId());
        return genreMapper.toDetailsDto(created);
    }

    @Transactional(readOnly = true)
    @Override
    public GenreDetailsDto getById(Long id) throws ResourceNotFoundException {
        log.info("Getting genre id={}", id);
        GenreDetailsDto genreDetailsDto = genreMapper.toDetailsDto(getByIdOrThrow(id));
        log.info("Genre found id={}", id);
        return genreDetailsDto;
    }

    @Transactional(readOnly = true)
    @Override
    public PageDto<GenreInfoDto> search(GenreFilter filter) {
        log.info("Searching genres by filter {}", filter);
        Pageable pageable = PageUtil.pageableFrom(filter, paginationProps);
        Specification<Genre> spec = SpecificationFactory.createForGenre(filter);
        Page<Genre> genres = genreRepository.findAll(spec, pageable);
        log.info("{}", genres);
        return new PageDto<>(genres.map(genreMapper::toInfoDto));
    }

    @Override
    public GenreDetailsDto updateById(Long id, GenreRequestDto body)
        throws ResourceNotFoundException, ResourceAlreadyExistsException {
        log.info("Updating genre id={}", id);

        Genre updated = transactionTemplate.execute(status -> update(id, body));

        log.info("Updated genre id={}", id);
        return genreMapper.toDetailsDto(updated);
    }

    @Transactional
    @Override
    public void deleteById(Long id) throws ResourceNotFoundException {
        log.info("Deleting genre id={}", id);
        getByIdOrThrow(id);
        genreRepository.deleteById(id);
        log.info("Deleted genre id={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Genre> getAllByIds(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return genreRepository.findAllById(ids);
    }

    @Transactional(readOnly = true)
    public Genre getByIdOrThrow(Long id) throws ResourceNotFoundException {
        return genreRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("%d not found".formatted(id), id, "Genre"));
    }

    private Genre createGenre(GenreRequestDto body) {
        checkNotExistsNameOrThrow(body.name());
        Genre genre = genreMapper.toEntity(body);
        return genreRepository.save(genre);
    }

    private Genre update(Long id, GenreRequestDto body) {
        Genre found = getByIdOrThrow(id);

        boolean isUpdated = false;

        if (body.name() != null && !body.name().equals(found.getName())) {
            checkNotExistsNameOrThrow(body.name());
            found.setName(body.name());
            isUpdated = true;
        }

        if (isUpdated) {
            return genreRepository.save(found);
        }
        return found;
    }

    @Transactional(readOnly = true)
    private void checkNotExistsNameOrThrow(String name) throws ResourceAlreadyExistsException {
        if (name == null) return;
        Optional<Genre> opt = genreRepository.findByName(name);
        if (opt.isPresent()) {
            Genre genre = opt.get();
            throw new ResourceAlreadyExistsException(
                "Name already exists",
                genre.getId(), "Genre", name);
        }
    }

}
