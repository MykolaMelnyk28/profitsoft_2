package com.melnyk.profitsoft_2.controller;

import com.melnyk.profitsoft_2.dto.request.BookRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.BookFilter;
import com.melnyk.profitsoft_2.dto.response.BookDetailsDto;
import com.melnyk.profitsoft_2.dto.response.BookInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.dto.response.UploadResponse;
import com.melnyk.profitsoft_2.service.BookService;
import com.melnyk.profitsoft_2.util.URIUtil;
import com.melnyk.profitsoft_2.validaton.Groups;
import com.melnyk.profitsoft_2.validaton.JsonFile;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/api/books")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Books", description = "API for managing books")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(
        summary = "Create book",
        description = "Create book",
        responses = {
            @ApiResponse(responseCode = "201", description = "Book created",
                headers = @Header(name = "Location"),
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BookDetailsDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "authorId or some genreId not found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict - book with specify combination title and authorId already exist",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)))
        })
    public ResponseEntity<BookDetailsDto> createBook(
        @RequestBody @Validated(Groups.OnCreate.class) BookRequestDto body,
        UriComponentsBuilder uriBuilder
    ) {
        BookDetailsDto created = bookService.create(body);
        URI uri = URIUtil.createLocationUri(uriBuilder, "/api/books", created.getId());
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get book details",
        description = "Retrieves book details by book ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Book found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BookDetailsDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Book not found",
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
    public ResponseEntity<BookDetailsDto> getBookById(
        @PathVariable @Min(1) Long id
    ) {
        return ResponseEntity.ok(bookService.getById(id));
    }

    @PostMapping("/_list")
    @Operation(
        summary = "Search books",
        description = "Retrieves a paginated list of books based on filter criteria",
        responses = {
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully",
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
    public ResponseEntity<PageDto<BookInfoDto>> searchBooks(
        @RequestBody @Valid BookFilter filter
    ) {
        PageDto<BookInfoDto> page = bookService.search(filter);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update book",
        description = "Updates book by ID",
        responses = {
            @ApiResponse(responseCode = "200", description = "Book updated",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = BookDetailsDto.class))
            ),
            @ApiResponse(responseCode = "404", description = "Book not found or authorId or some genreId not found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "409", description = "Conflict - book with specify combination title and authorId already exist",
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
    public ResponseEntity<BookDetailsDto> updateBookById(
        @PathVariable @Min(1) Long id,
        @RequestBody @Validated(Groups.OnUpdate.class) BookRequestDto body
    ) {
        BookDetailsDto book = bookService.updateById(id, body);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete book",
        description = "Deletes book by ID",
        responses = {
            @ApiResponse(responseCode = "204", description = "Book deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
            ),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class))
            )
        }
    )
    public ResponseEntity<?> deleteBookById(
        @PathVariable @Min(1) Long id
    ) {
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/_report", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(
        summary = "Generates a book report",
        description = "Generates a report (Excel) based on the provided filter",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "Report file successfully generated",
                content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE)
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid filter",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
                )
            )
        }
    )
    public void generateBookReport(
        @RequestBody @Valid BookFilter filter,
        HttpServletResponse response
    ) throws IOException {
        bookService.generateReport(filter, response);
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Uploads books from a file",
        description = "Accepts a JSON file and creates books",
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "File processed successfully",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = UploadResponse.class)
                )
            ),
            @ApiResponse(
                responseCode = "400",
                description = "Invalid file",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
                )
            ),
            @ApiResponse(
                responseCode = "500",
                description = "Internal server error",
                content = @Content(
                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                    schema = @Schema(implementation = ProblemDetail.class)
                )
            )
        }
    )
    public ResponseEntity<UploadResponse> uploadBooks(
        @Parameter(
            name = "file",
            description = "JSON file with array of BookRequestDto",
            required = true
        )
        @RequestPart("file") @Valid @JsonFile MultipartFile file
    ) throws IOException {
        UploadResponse response = bookService.uploadFromFile(file);
        return ResponseEntity.ok(response);
    }

}
