package com.melnyk.profitsoft_2.controller;

import com.melnyk.profitsoft_2.Profitsoft2Application;
import com.melnyk.profitsoft_2.config.TestConfig;
import com.melnyk.profitsoft_2.dto.request.BookRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.BookFilter;
import com.melnyk.profitsoft_2.dto.response.*;
import com.melnyk.profitsoft_2.entity.Author;
import com.melnyk.profitsoft_2.entity.Book;
import com.melnyk.profitsoft_2.entity.Genre;
import com.melnyk.profitsoft_2.mapper.AuthorMapper;
import com.melnyk.profitsoft_2.mapper.BookMapper;
import com.melnyk.profitsoft_2.mapper.GenreMapper;
import com.melnyk.profitsoft_2.repository.AuthorRepository;
import com.melnyk.profitsoft_2.repository.BookRepository;
import com.melnyk.profitsoft_2.repository.GenreRepository;
import com.melnyk.profitsoft_2.util.DataUtil;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThatIterable;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Profitsoft2Application.class)
@AutoConfigureMockMvc
@Import(TestConfig.class)
@AutoConfigureEmbeddedDatabase(
    provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.DEFAULT,
    type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
    refresh = AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD,
    beanName = "datasource"
)
@ActiveProfiles("test")
class BookControllerIT {

    static final Map<Long, Book> BOOKS = new HashMap<>();
    static final Map<Long, Genre> GENRES = new HashMap<>();
    static final Map<Long, Author> AUTHORS = new HashMap<>();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    BookRepository bookRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    GenreMapper genreMapper;

    @Autowired
    BookMapper bookMapper;

    @Autowired
    AuthorMapper authorMapper;

    boolean isInitialized = false;

    @BeforeEach
    void beforeEach() {
        if (!isInitialized) {
            DataUtil.saveDefaultGenres(objectMapper, genreRepository).forEach(x -> GENRES.put(x.getId(), x));
            DataUtil.saveDefaultAuthors(objectMapper, authorRepository).forEach(x -> AUTHORS.put(x.getId(), x));
        }
        DataUtil.saveDefaultBooks(objectMapper, bookRepository, authorRepository, genreRepository)
            .forEach(x -> BOOKS.put(x.getId(), x));
        isInitialized = true;
    }

    @AfterEach
    void afterEach() {
        BOOKS.clear();
        bookRepository.deleteAll();
    }

    // getBookById

    @Test
    void getBookById_givenExistingId_returnsBookWith200() throws Exception {
        Long id = 3L;
        Book foundBook = BOOKS.get(id);
        BookDetailsDto expectedResponseBody = bookMapper.toDetailsDto(foundBook);

        mockMvc.perform(get("/api/books/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    void getBookById_givenNotExistingId_returns404() throws Exception {
        Long id = 9999L;

        mockMvc.perform(get("/api/books/{id}", id))
            .andExpect(status().isNotFound());
    }

    // createBook

    @Test
    void createBook_givenValidRequest_returnsCreatedWith201() throws Exception {
        Long authorId = 1L;
        Author author = AUTHORS.get(authorId);
        AuthorInfoDto authorInfoDto = authorMapper.toInfoDto(author);
        Set<Long> genreIds = Set.of(1L, 2L, 3L);
        Set<GenreInfoDto> genres = genreIds.stream().map(GENRES::get).map(genreMapper::toInfoDto).collect(Collectors.toSet());
        BookRequestDto requestBody = new BookRequestDto(
            "newBook",
            "newDescription",
            authorId,
            2019,
            651,
            genreIds
        );

        String jsonResponse = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andReturn()
            .getResponse()
            .getContentAsString();

        BookDetailsDto response = objectMapper.readValue(jsonResponse, BookDetailsDto.class);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getTitle()).isEqualTo(requestBody.title());
        assertThat(response.getAuthor()).isEqualTo(authorInfoDto);
        assertThatIterable(new HashSet<>(response.getGenres())).isEqualTo(genres);

        Book saved = bookRepository.findById(response.getId()).orElseThrow();
        assertThat(saved.getTitle()).isEqualTo("newBook");
    }

    @Test
    void createBook_givenEmptyRequest_returns400() throws Exception {
        mockMvc.perform(post("/api/books"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createBook_givenNotValidRequest_returns400() throws Exception {
        BookRequestDto requestBody = new BookRequestDto(
            null,
            null,
            null,
            null,
            null,
            null
        );

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createBook_givenExistingBook_returns409() throws Exception {
        Book existingBook = BOOKS.get(1L);
        BookRequestDto requestBody = new BookRequestDto(
            existingBook.getTitle(),
            "desc",
            existingBook.getAuthor().getId(),
            2013,
            701,
            Set.of(1L)
        );

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isConflict());
    }

    // searchBooks

    @Test
    void searchBooks_givenNotValidRequest_returns400() throws Exception {
        mockMvc.perform(post("/api/books/_list")
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchBooks_givenValidRequestWithFilterValueAndDefaultPagination_returnsBooksWith200() throws Exception {
        int expectedTotalElements = 23;
        BookFilter filter = new BookFilter(
            "F",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        testSearchBooks(filter, expectedTotalElements, Comparator.comparingLong(BookInfoDto::getId));
    }

    @Test
    void searchBooks_givenValidRequestWithFilterValueAndCustomPagination_returnsBooksWith200() throws Exception {
        int expectedTotalElements = 23;
        BookFilter filter = new BookFilter(
            "F",
            null,
            null,
            null,
            null,
            null,
            null,
            2,
            3,
            "id,asc",
            null,
            null,
            null,
            null
        );

        testSearchBooks(filter, expectedTotalElements, Comparator.comparingLong(BookInfoDto::getId));
    }

    @Test
    void searchBooks_givenSortParamByMissingField_returnsBooksWith400() throws Exception {
        String invalidSort = "field,asc";
        mockMvc.perform(post("/api/books/_list")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sort\":\"%s\"}".formatted(invalidSort)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchBooks_givenSortParamByMissingDirection_returnsBooksWith400() throws Exception {
        String invalidSort = "id,direction";
        mockMvc.perform(post("/api/books/_list")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sort\":\"%s\"}".formatted(invalidSort)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchBooks_givenSortParamWithIgnoreCaseValue_returnsBooksWith200() throws Exception {
        int expectedTotalElements = 23;
        BookFilter filter = new BookFilter(
            "f",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "Id,AsC",
            null,
            null,
            null,
            null
        );

        testSearchBooks(filter, expectedTotalElements, Comparator.comparingLong(BookInfoDto::getId));
    }

    // updateBookById

    @Test
    void updateBookById_givenExistingIdAndValidRequest_returnsUpdatedBookWith200() throws Exception {
        Long existingBookId = 1L;
        Book existingBook = BOOKS.get(existingBookId);
        AuthorInfoDto authorInfoDto = authorMapper.toInfoDto(existingBook.getAuthor());
        Set<GenreInfoDto> genres = existingBook.getGenres().stream()
            .map(genreMapper::toInfoDto)
            .collect(Collectors.toSet());

        BookRequestDto requestBody = new BookRequestDto(
            existingBook.getTitle(),
            "updatedDescription",
            existingBook.getAuthor().getId(),
            existingBook.getYearPublished(),
            existingBook.getPages(),
            existingBook.getGenres().stream().map(Genre::getId).collect(Collectors.toSet())
        );

        BookDetailsDto expectedResponseBody = new BookDetailsDto(
            null,
            requestBody.title(),
            requestBody.description(),
            authorInfoDto,
            requestBody.yearPublished(),
            requestBody.pages(),
            null,
            null,
            null
        );

        String resposneJson = mockMvc.perform(put("/api/books/{id}", existingBookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

        BookDetailsDto updated = objectMapper.readValue(resposneJson, BookDetailsDto.class);

        assertThat(updated.getId()).isPositive();

        assertThat(updated)
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt", "id", "genres")
            .isEqualTo(expectedResponseBody);

        assertThat(updated.getGenres())
            .containsExactlyInAnyOrderElementsOf(genres);
    }

    @Test
    void updateBookById_givenNotExistingIdAndValidRequest_returns404() throws Exception {
        Long id = 9999L;

        BookRequestDto requestBody = new BookRequestDto(
            "title",
            "description",
            1L,
            2010,
            500,
            Set.of(1L)
        );

        mockMvc.perform(put("/api/books/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateBookById_givenExistingIdAndEmptyRequest_returns400() throws Exception {
        Long id = 1L;

        mockMvc.perform(put("/api/books/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateBookById_givenExistingIdAndInvalidRequest_returns400() throws Exception {
        Long id = 1L;

        mockMvc.perform(put("/api/books/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    // deleteBookById

    @Test
    void deleteBookById_givenExistingId_returns204() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/api/books/{id}", id))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteBookById_givenNotExistingId_returns404() throws Exception {
        Long id = 9999L;

        mockMvc.perform(delete("/api/books/{id}", id))
            .andExpect(status().isNotFound());
    }

    void testSearchBooks(BookFilter filter, int expectedTotalElements, Comparator<BookInfoDto> comparator) throws Exception {
        int page = filter.page() != null ? filter.page() : 0;
        int size = filter.size() != null ? filter.size() : 10;
        int expectedTotalPages = (int)Math.ceil((double) expectedTotalElements / size);

        List<BookInfoDto> expectedBooks = BOOKS.values()
            .stream()
            .filter(x -> filter.title() == null || x.getTitle().toLowerCase().contains(filter.title().toLowerCase()))
            .skip((long) size * page)
            .limit(size)
            .map(bookMapper::toInfoDto)
            .sorted(comparator)
            .toList();

        PageDto<BookInfoDto> expectedResponseBody = new PageDto<>(
            expectedBooks,
            page,
            size,
            expectedTotalElements,
            expectedTotalPages
        );

        mockMvc.perform(post("/api/books/_list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

//
//    @Test
//    void createBook_givenValidRequest_returnsCreatedWith201() {}
//
//    @Test
//    void getBookById_givenExistingId_returnsBookWith200() {}
//
//    @Test
//    void updateBookById_givenExistingIdAndValidRequest_returnsUpdatedBookWith200() {}
//
//    @Test
//    void deleteBookById_givenExistingId_returnsNoContentWith204() {}
//
//    @Test
//    void searchBooks_givenValidFilters_returnsBooksWith200() {}
//
//    @Test
//    void reportBooks_givenValidFilters_returnsExcelFile() {}
//
//    @Test
//    void uploadBooks_givenValidJsonFile_returnsUploadResultWithDetails() {}

}
