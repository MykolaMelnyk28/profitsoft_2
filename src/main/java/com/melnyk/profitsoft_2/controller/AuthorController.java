package com.melnyk.profitsoft_2.controller;

import com.melnyk.profitsoft_2.dto.request.AuthorRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.AuthorFilter;
import com.melnyk.profitsoft_2.dto.response.AuthorDetailsDto;
import com.melnyk.profitsoft_2.dto.response.AuthorInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.service.AuthorService;
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
@RequestMapping("/api/authors")
@Slf4j
@RequiredArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    public ResponseEntity<AuthorDetailsDto> createAuthor(
        @RequestBody @Validated(Groups.OnCreate.class) AuthorRequestDto body,
        UriComponentsBuilder uriBuilder
    ) {
        log.info("POST /api/authors body={}", body);
        AuthorDetailsDto created = authorService.create(body);
        URI uri = URIUtil.createLocationUri(uriBuilder, "/api/authors", created.getId());
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDetailsDto> getAuthorById(
        @PathVariable @Min(1) Long id
    ) {
        log.info("GET /api/authors/{}", id);
        return ResponseEntity.ok(authorService.getById(id));
    }

    @PostMapping("/_list")
    public ResponseEntity<PageDto<AuthorInfoDto>> searchAuthors(
        @RequestBody @Valid AuthorFilter filter
    ) {
        log.info("POST /api/authors/_list body={}", filter);
        PageDto<AuthorInfoDto> authors = authorService.search(filter);
        return ResponseEntity.ok(authors);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AuthorDetailsDto> updateAuthorById(
        @PathVariable @Min(1) Long id,
        @RequestBody @Validated(Groups.OnUpdate.class) AuthorRequestDto body
    ) {
        log.info("PUT /api/authors/{} body={}", id, body);
        AuthorDetailsDto genre = authorService.updateById(id, body);
        return ResponseEntity.ok(genre);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AuthorDetailsDto> deleteAuthorById(
        @PathVariable @Min(1) Long id
    ) {
        log.info("DELETE /api/authors/{}", id);
        authorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
