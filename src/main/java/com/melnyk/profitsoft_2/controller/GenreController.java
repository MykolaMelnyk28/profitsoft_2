package com.melnyk.profitsoft_2.controller;

import com.melnyk.profitsoft_2.dto.request.filter.impl.GenreFilter;
import com.melnyk.profitsoft_2.dto.request.GenreRequestDto;
import com.melnyk.profitsoft_2.dto.response.GenreDetailsDto;
import com.melnyk.profitsoft_2.dto.response.GenreInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.service.GenreService;
import com.melnyk.profitsoft_2.util.URIUtil;
import com.melnyk.profitsoft_2.validaton.Groups;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/genres")
@Slf4j
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/{id}")
    public ResponseEntity<GenreDetailsDto> getGenreById(
        @PathVariable @Min(1) Long id
    ) {
        log.info("GET /api/genres/{}", id);
        return ResponseEntity.ok(genreService.getById(id));
    }

    @PostMapping
    public ResponseEntity<GenreDetailsDto> createGenre(
        @RequestBody @Validated(Groups.OnCreate.class) GenreRequestDto body,
        UriComponentsBuilder uriBuilder
    ) {
        log.info("POST /api/genres body={}", body);
        GenreDetailsDto created = genreService.create(body);
        URI uri = URIUtil.createLocationUri(uriBuilder, "/api/genres", created.getId());
        return ResponseEntity.created(uri).body(created);
    }

    @PostMapping("/_list")
    public ResponseEntity<PageDto<GenreInfoDto>> searchGenres(
        @RequestBody @Valid GenreFilter filter
    ) {
        log.info("POST /api/genres/_list body={}", filter);
        PageDto<GenreInfoDto> genres = genreService.search(filter);
        return ResponseEntity.ok(genres);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GenreDetailsDto> updateGenreById(
        @PathVariable @Min(1) Long id,
        @RequestBody @Validated(Groups.OnUpdate.class) GenreRequestDto body
    ) {
        log.info("PUT /api/genres/{} body={}", id, body);
        GenreDetailsDto genre = genreService.updateById(id, body);
        return ResponseEntity.ok(genre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGenreById(
        @PathVariable @Min(1) Long id
    ) {
        log.info("DELETE /api/genres/{}", id);
        genreService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
