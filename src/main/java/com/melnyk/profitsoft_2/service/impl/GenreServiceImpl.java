package com.melnyk.profitsoft_2.service.impl;

import com.melnyk.profitsoft_2.config.CacheConfig;
import com.melnyk.profitsoft_2.config.aspect.LogServiceMethod;
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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;
    private final TransactionTemplate transactionTemplate;
    private final PaginationProps paginationProps;
    private final CacheManager cacheManager;

    @Override
    @LogServiceMethod(logArgs = true)
    public GenreDetailsDto create(GenreRequestDto body) throws ResourceAlreadyExistsException {
        Genre created = createGenre(body);
        return genreMapper.toDetailsDto(created);
    }

    @Transactional(readOnly = true)
    @Override
    @LogServiceMethod(logArgs = true)
    public GenreDetailsDto getById(Long id) throws ResourceNotFoundException {
        GenreDetailsDto genreDetailsDto = genreMapper.toDetailsDto(getByIdOrThrow(id));
        return genreDetailsDto;
    }

    @Transactional(readOnly = true)
    @Override
    @LogServiceMethod(logArgs = true)
    public PageDto<GenreInfoDto> search(GenreFilter filter) {
        Pageable pageable = PageUtil.pageableFrom(filter, paginationProps);
        Specification<Genre> spec = SpecificationFactory.createForGenre(filter);
        Page<Genre> genres = genreRepository.findAll(spec, pageable);
        return new PageDto<>(genres.map(genreMapper::toInfoDto));
    }

    @Override
    @LogServiceMethod(logArgs = true)
    public GenreDetailsDto updateById(Long id, GenreRequestDto body)
        throws ResourceNotFoundException, ResourceAlreadyExistsException {
        Genre updated = update(id, body);
        return genreMapper.toDetailsDto(updated);
    }

    @Transactional
    @Override
    @LogServiceMethod(logArgs = true)
    public void deleteById(Long id) throws ResourceNotFoundException {
        Genre genre = getByIdOrThrow(id);
        genreRepository.deleteById(id);
        getCache().ifPresent(cache -> {
            cache.evictIfPresent(id);
            cache.evictIfPresent(genre.getName());
        });
    }

    @Override
    @Transactional(readOnly = true)
    @LogServiceMethod(logArgs = true)
    public List<Genre> getAllByIds(Collection<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }

        Map<Long, Genre> genreMap = new LinkedHashMap<>();
        List<Long> missingIds = new ArrayList<>();

        Cache cache = getCache().orElse(null);

        for (Long id : ids) {
            Genre genre = (cache != null) ? cache.get(id, Genre.class) : null;

            if (genre == null) {
                missingIds.add(id);
            }

            genreMap.put(id, genre);
        }

        if (!missingIds.isEmpty()) {
            List<Genre> foundGenres = genreRepository.findAllById(missingIds);

            Map<Long, Genre> foundMap = foundGenres.stream()
                .collect(Collectors.toMap(Genre::getId, g -> g));

            for (Long id : missingIds) {
                Genre genre = foundMap.get(id);

                if (genre == null) {
                    throw new ResourceNotFoundException("%d not found".formatted(id), id, "Genre");
                }

                genreMap.put(id, genre);

                if (cache != null) {
                    cache.put(id, genre);
                }
            }
        }

        return new ArrayList<>(genreMap.values());
    }

    @Transactional(readOnly = true)
    @LogServiceMethod(logArgs = true)
    public Genre getByIdOrThrow(Long id) throws ResourceNotFoundException {
        Optional<Cache> cacheOpt = getCache();
        return cacheOpt
            .map(cache -> cache.get(id, Genre.class))
            .or(() -> {
                Optional<Genre> opt = genreRepository.findById(id);
                opt.ifPresent(g -> cacheOpt.ifPresent(cache -> cache.put(id, g)));
                return opt;
            })
            .orElseThrow(() -> new ResourceNotFoundException("%d not found".formatted(id), id, "Genre"));
    }

    private Genre createGenre(GenreRequestDto body) {
        Genre created = transactionTemplate.execute(status -> {
            checkNotExistsNameOrThrow(body.name());
            Genre genre = genreMapper.toEntity(body);
            return genreRepository.save(genre);
        });

        getCache().ifPresent(cache -> cache.put(created.getId(), created));

        return created;
    }

    private Genre update(Long id, GenreRequestDto body) {
        return transactionTemplate.execute(status -> {
            Genre found = getByIdOrThrow(id);

            boolean isUpdated = false;

            if (body.name() != null && !body.name().equals(found.getName())) {
                checkNotExistsNameOrThrow(body.name());
                found.setName(body.name());
                isUpdated = true;
            }

            if (isUpdated) {
                Genre updated = genreRepository.save(found);
                getCache().ifPresent(cache -> cache.put(id, updated));
                return updated;
            }
            return found;
        });
    }

    @Transactional(readOnly = true)
    private void checkNotExistsNameOrThrow(String name) throws ResourceAlreadyExistsException {
        if (name == null) return;
        Optional<Genre> opt = getByName(name);
        if (opt.isPresent()) {
            Genre genre = opt.get();
            throw new ResourceAlreadyExistsException(
                "Name already exists",
                genre.getId(), "Genre", name);
        }
    }

    private Optional<Genre> getByName(String name) {
        Optional<Cache> cacheOpt = getCache();
        return cacheOpt
            .map(cache -> cache.get(name, Genre.class))
            .or(() -> {
                Optional<Genre> opt = genreRepository.findByName(name);
                opt.ifPresent(g -> cacheOpt.ifPresent(cache -> cache.put(name, g)));
                return opt;
            });
    }

    private Optional<Cache> getCache() {
        return Optional.ofNullable(cacheManager)
            .map(x -> x.getCache(CacheConfig.GENRE_CACHE_NAME));
    }

}
