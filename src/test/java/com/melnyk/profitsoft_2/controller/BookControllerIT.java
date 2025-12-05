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
import com.melnyk.profitsoft_2.util.ResourceUtil;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.MappingIterator;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThatIterable;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = Profitsoft2Application.class)
@AutoConfigureMockMvc
@Import(TestConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureEmbeddedDatabase(
    provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.DEFAULT,
    type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
    beanName = "bookDatasource"
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

    @Autowired
    CacheManager cacheManager;

    Instant initializedTime;

    @BeforeAll
    @Transactional
    void beforeEach() {
        if (initializedTime == null) {
            initializedTime = Instant.now();
            DataUtil.saveDefaultGenres(objectMapper, genreRepository).forEach(x -> GENRES.put(x.getId(), x));
            DataUtil.saveDefaultAuthors(objectMapper, authorRepository).forEach(x -> AUTHORS.put(x.getId(), x));
        }
        DataUtil.saveDefaultBooks(objectMapper, bookRepository, authorRepository, genreRepository)
            .forEach(x -> BOOKS.put(x.getId(), x));
        for(String name : cacheManager.getCacheNames()) {
            cacheManager.getCache(name).clear();
        }
    }

    // getBookById

    @Test
    @Transactional(readOnly = true)
    void getBookById_givenExistingId_returnsBookWith200() throws Exception {
        Long id = 3L;
        Book foundBook = BOOKS.get(id);
        BookDetailsDto expectedResponseBody = bookMapper.toDetailsDto(foundBook);

        String jsonResponse = mockMvc.perform(get("/api/books/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

        BookDetailsDto responseBody = objectMapper.readValue(jsonResponse, BookDetailsDto.class);

        assertThat(responseBody)
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedResponseBody);

        assertThat(responseBody.getCreatedAt()).isAfterOrEqualTo(initializedTime);
        assertThat(responseBody.getUpdatedAt()).isAfterOrEqualTo(initializedTime);
    }

    @Test
    @Transactional(readOnly = true)
    void getBookById_givenNotExistingId_returns404() throws Exception {
        Long id = 9999L;

        mockMvc.perform(get("/api/books/{id}", id))
            .andExpect(status().isNotFound());
    }

    // createBook

    @Test
    @Transactional
    @Rollback
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

        Instant preRequestTime = Instant.now();
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

        assertThat(response.getCreatedAt()).isAfterOrEqualTo(preRequestTime);
        assertThat(response.getUpdatedAt()).isAfterOrEqualTo(preRequestTime);
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
    @Transactional
    @Rollback
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional
    @Rollback
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
    @Transactional(readOnly = true)
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
    @Transactional
    @Rollback
    void deleteBookById_givenExistingId_returns204() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/api/books/{id}", id))
            .andExpect(status().isNoContent());
    }

    @Test
    @Transactional(readOnly = true)
    void deleteBookById_givenNotExistingId_returns404() throws Exception {
        Long id = 9999L;

        mockMvc.perform(delete("/api/books/{id}", id))
            .andExpect(status().isNotFound());
    }

    // generateBookReport

    @Test
    @Transactional(readOnly = true)
    void generateBookReport_givenValidFilters_returnsExcelFileWith200() throws Exception {
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
            "id, asc",
            null,
            null,
            null,
            null
        );

        testGenerationExcelBookReport(filter, expectedTotalElements, Comparator.comparingLong(BookInfoDto::getId));
    }

    // uploadBooks

    @Test
    void uploadBooks_givenMissingFileRequestPart_returns400() throws Exception {
        mockMvc.perform(multipart("/api/books/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @Rollback
    void uploadBooks_givenJSONMultipartFileWithEmptyArray_returnsUploadResponseWith200() throws Exception {
        UploadResponse expectedResponse = new UploadResponse(0, 0, 0, List.of());

        MockMultipartFile file = new MockMultipartFile(
            "file",
            "books.json",
            "application/json",
            "[]".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/api/books/upload")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .file(file))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));
    }

    @Test
    @Transactional
    @Rollback
    void uploadBooks_givenValidJSONMultipartFile_returnsUploadResponseWith200() throws Exception {
        Path jsonFilePath = ResourceUtil.getResourcePath("upload.json");
        testUploadJSONFile(jsonFilePath, 10, 0);
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

    void testGenerationExcelBookReport(BookFilter filter, int expectedTotalElements, Comparator<BookInfoDto> comparator) throws Exception {
        List<BookInfoDto> expectedBooks = BOOKS.values()
            .stream()
            .filter(x -> filter.title() == null || x.getTitle().toLowerCase().contains(filter.title().toLowerCase()))
            .map(bookMapper::toInfoDto)
            .sorted(comparator)
            .toList();

        Map<String, CellType> expectedHeaderNames = new LinkedHashMap<>();
        expectedHeaderNames.put("id", CellType.NUMERIC);
        expectedHeaderNames.put("title", CellType.STRING);
        expectedHeaderNames.put("description", CellType.STRING);
        expectedHeaderNames.put("author", CellType.STRING);
        expectedHeaderNames.put("yearPublished", CellType.NUMERIC);
        expectedHeaderNames.put("pages", CellType.NUMERIC);
        expectedHeaderNames.put("genres", CellType.STRING);

        var response = mockMvc.perform(post("/api/books/_report")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_OCTET_STREAM)
                .content(objectMapper.writeValueAsString(filter)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andReturn()
            .getResponse();

        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(response.getContentAsByteArray());
             XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheet("books");
            assertThat(sheet).isNotNull();

            Iterator<Row> iter = sheet.rowIterator();
            assertThat(iter.hasNext()).isTrue();

            // headers check

            Row headerRow = iter.next();
            List<String> actualHeaderNames = new ArrayList<>();
            headerRow.forEach(c -> actualHeaderNames.add(c.getStringCellValue()));

            assertThat(actualHeaderNames).containsExactlyElementsOf(expectedHeaderNames.keySet());

            // rows check

            int rowIndex = 0;
            while (iter.hasNext()) {
                Row row = iter.next();
                BookInfoDto expected = expectedBooks.get(rowIndex++);

                int cellIndex = 0;

                assertThat(row.getCell(cellIndex++).getNumericCellValue())
                    .isEqualTo(expected.getId().doubleValue());

                assertThat(row.getCell(cellIndex++).getStringCellValue())
                    .isEqualTo(expected.getTitle());

                assertThat(row.getCell(cellIndex++).getStringCellValue())
                    .isEqualTo(expected.getDescription());

                var author = expected.getAuthor();
                String authorFullName = author.getFirstName() + " " + author.getLastName();
                assertThat(row.getCell(cellIndex++).getStringCellValue())
                    .isEqualTo(authorFullName);

                assertThat((int) row.getCell(cellIndex++).getNumericCellValue())
                    .isEqualTo(expected.getYearPublished());

                assertThat((int) row.getCell(cellIndex++).getNumericCellValue())
                    .isEqualTo(expected.getPages());

                assertThat(row.getCell(cellIndex++).getStringCellValue())
                    .isEqualTo(expected.getGenres().stream()
                        .map(GenreInfoDto::getName)
                        .collect(Collectors.joining(",")));
            }
            assertThat(rowIndex).isEqualTo(expectedTotalElements);
        }
    }

    void testUploadJSONFile(Path jsonFilePath, int expectedCreatedCount, int expectedFailedCount)
        throws Exception {
        InputStream inputStream = Files.newInputStream(jsonFilePath);

        MockMultipartFile file = new MockMultipartFile(
            "file",
            jsonFilePath.getFileName().toString(),
            MediaType.APPLICATION_JSON_VALUE,
            inputStream
        );

        UploadResponse expectedResponse = new UploadResponse();
        expectedResponse.setTotalCount(expectedCreatedCount + expectedFailedCount);
        expectedResponse.setCreatedCount(expectedCreatedCount);
        expectedResponse.setFailedCount(expectedFailedCount);
        expectedResponse.setFailedItems(List.of());

        mockMvc.perform(multipart("/api/books/upload")
                .file(file))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponse)));

        try (InputStream reStream = Files.newInputStream(jsonFilePath)) {
            MappingIterator<BookRequestDto> it = objectMapper
                .readerFor(BookRequestDto.class)
                .readValues(reStream);

            while (it.hasNext()) {
                BookRequestDto dto = it.next();
                assertThat(bookRepository.findByTitleAndAuthorId(dto.title(), dto.authorId())).isPresent();
            }
        }
    }

}
