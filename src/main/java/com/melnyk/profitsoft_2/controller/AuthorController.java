package com.melnyk.profitsoft_2.controller;

import com.melnyk.profitsoft_2.dto.request.AuthorRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.AuthorFilter;
import com.melnyk.profitsoft_2.dto.response.AuthorDetailsDto;
import com.melnyk.profitsoft_2.dto.response.AuthorInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.service.AuthorService;
import com.melnyk.profitsoft_2.util.URIUtil;
import com.melnyk.profitsoft_2.validaton.Groups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/authors")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Authors", description = "API for managing authors")
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping
    @Operation(
        summary = "Create author",
        description = "Create author",
        responses = {
            @ApiResponse(responseCode = "201", description = "Author created",
                headers = @Header(name = "Location"),
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthorDetailsDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
        })
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
    @Operation(
        summary = "Get author details",
        description = "Retrieves author details by author ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Author found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthorDetailsDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Author not found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
        }
    )
    public ResponseEntity<AuthorDetailsDto> getAuthorById(
        @PathVariable @Min(1) Long id
    ) {
        log.info("GET /api/authors/{}", id);
        return ResponseEntity.ok(authorService.getById(id));
    }

    @PostMapping("/_list")
    @Operation(
        summary = "Search authors",
        description = "Retrieves a paginated list of authors based on filter criteria",
        responses = {
            @ApiResponse(responseCode = "200", description = "Authors retrieved successfully",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PageDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<PageDto<AuthorInfoDto>> searchAuthors(
        @RequestBody @Valid AuthorFilter filter
    ) {
        log.info("POST /api/authors/_list body={}", filter);
        PageDto<AuthorInfoDto> authors = authorService.search(filter);
        return ResponseEntity.ok(authors);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update author",
        description = "Updates author by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Author updated",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = AuthorDetailsDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Author not found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<AuthorDetailsDto> updateAuthorById(
        @PathVariable @Min(1) Long id,
        @RequestBody @Validated(Groups.OnUpdate.class) AuthorRequestDto body
    ) {
        log.info("PUT /api/authors/{} body={}", id, body);
        AuthorDetailsDto genre = authorService.updateById(id, body);
        return ResponseEntity.ok(genre);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete author",
        description = "Deletes author by ID",
        responses = {
            @ApiResponse(responseCode = "204", description = "Author deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Author not found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<AuthorDetailsDto> deleteAuthorById(
        @PathVariable @Min(1) Long id
    ) {
        log.info("DELETE /api/authors/{}", id);
        authorService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
