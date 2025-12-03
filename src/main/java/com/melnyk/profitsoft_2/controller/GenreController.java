package com.melnyk.profitsoft_2.controller;

import com.melnyk.profitsoft_2.dto.request.GenreRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.GenreFilter;
import com.melnyk.profitsoft_2.dto.response.GenreDetailsDto;
import com.melnyk.profitsoft_2.dto.response.GenreInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.service.GenreService;
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
@RequestMapping("/api/genres")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Genres", description = "API for managing genres")
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/{id}")
    @Operation(
        summary = "Get genre details",
        description = "Retrieves genre details by genre ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Genre found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GenreDetailsDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Genre not found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<GenreDetailsDto> getGenreById(
        @PathVariable @Min(1) Long id
    ) {
        log.info("GET /api/genres/{}", id);
        return ResponseEntity.ok(genreService.getById(id));
    }

    @PostMapping
    @Operation(
        summary = "Create genre",
        description = "Create genre",
        responses = {
            @ApiResponse(responseCode = "201", description = "Genre created",
                headers = @Header(name = "Location"),
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GenreDetailsDto.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict - genre with specify name already exist",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
        })
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
    @Operation(
        summary = "Search genres",
        description = "Retrieves a paginated list of genres based on filter criteria",
        responses = {
            @ApiResponse(responseCode = "200", description = "Genres retrieved successfully",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = PageDto.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
        }
    )
    public ResponseEntity<PageDto<GenreInfoDto>> searchGenres(
        @RequestBody @Valid GenreFilter filter
    ) {
        log.info("POST /api/genres/_list body={}", filter);
        PageDto<GenreInfoDto> genres = genreService.search(filter);
        return ResponseEntity.ok(genres);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update genre",
        description = "Updates genre by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Genre updated",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = GenreDetailsDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Genre not found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict - genre with specified name already exists",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<GenreDetailsDto> updateGenreById(
        @PathVariable @Min(1) Long id,
        @RequestBody @Validated(Groups.OnUpdate.class) GenreRequestDto body
    ) {
        log.info("PUT /api/genres/{} body={}", id, body);
        GenreDetailsDto genre = genreService.updateById(id, body);
        return ResponseEntity.ok(genre);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete genre",
        description = "Deletes genre by ID",
        responses = {
            @ApiResponse(responseCode = "204", description = "Genre deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Genre not found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<?> deleteGenreById(
        @PathVariable @Min(1) Long id
    ) {
        log.info("DELETE /api/genres/{}", id);
        genreService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
