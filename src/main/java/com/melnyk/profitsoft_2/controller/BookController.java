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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
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
public class BookController {

    private final BookService bookService;

    @PostMapping
    public ResponseEntity<BookDetailsDto> createBook(
        @RequestBody @Validated(Groups.OnCreate.class) BookRequestDto body,
        UriComponentsBuilder uriBuilder
    ) {
        log.info("POST /api/books body={}", body);
        BookDetailsDto created = bookService.create(body);
        URI uri = URIUtil.createLocationUri(uriBuilder, "/api/books", created.getId());
        return ResponseEntity.created(uri).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDetailsDto> getBookById(
        @PathVariable @Min(1) Long id
    ) {
        log.info("GET /api/books/{}", id);
        return ResponseEntity.ok(bookService.getById(id));
    }

    @PostMapping("/_list")
    public ResponseEntity<PageDto<BookInfoDto>> searchBooks(
        @RequestBody @Valid BookFilter filter
    ) {
        log.info("POST /api/books/_list body={}", filter);
        PageDto<BookInfoDto> page = bookService.search(filter);
        return ResponseEntity.ok(page);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDetailsDto> updateBookById(
        @PathVariable @Min(1) Long id,
        @RequestBody @Validated(Groups.OnUpdate.class) BookRequestDto body
    ) {
        log.info("PUT /api/books/{} body={}", id, body);
        BookDetailsDto book = bookService.updateById(id, body);
        return ResponseEntity.ok(book);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBookById(
        @PathVariable @Min(1) Long id
    ) {
        log.info("DELETE /api/books/{}", id);
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/_report", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public void generateBookReport(
        @RequestBody @Valid BookFilter filter,
        HttpServletResponse response
    ) throws IOException {
        log.info("POST /api/books/_report body={}", filter);
        bookService.generateReport(filter, response);
    }

    @PostMapping("/upload")
    public ResponseEntity<UploadResponse> uploadBooks(
        @RequestPart("file") @Valid @JsonFile MultipartFile file
    ) throws IOException {

        log.info("POST /api/books/upload file={}", file.getName());
        UploadResponse response = bookService.uploadFromFile(file);
        return ResponseEntity.ok(response);
    }

}
