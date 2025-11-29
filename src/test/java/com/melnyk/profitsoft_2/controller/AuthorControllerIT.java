package com.melnyk.profitsoft_2.controller;

import com.melnyk.profitsoft_2.Profitsoft2Application;
import com.melnyk.profitsoft_2.config.TestConfig;
import com.melnyk.profitsoft_2.dto.request.AuthorRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.AuthorFilter;
import com.melnyk.profitsoft_2.dto.response.AuthorDetailsDto;
import com.melnyk.profitsoft_2.dto.response.AuthorInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.entity.Author;
import com.melnyk.profitsoft_2.mapper.AuthorMapper;
import com.melnyk.profitsoft_2.repository.AuthorRepository;
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

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
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
class AuthorControllerIT {

    static final Map<Long, Author> AUTHORS = new HashMap<>();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    AuthorRepository authorRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    AuthorMapper authorMapper;

    @BeforeEach
    void beforeEach() {
        DataUtil.saveDefaultAuthors(objectMapper, authorRepository).forEach(x -> AUTHORS.put(x.getId(), x));
    }

    @AfterEach
    void afterEach() {
        AUTHORS.clear();
        authorRepository.deleteAll();
    }

    // getAuthorById

    @Test
    void getAuthorById_givenExistingId_returnsAuthorWith200() throws Exception {
        Long id = 3L;
        Author foundAuthor = AUTHORS.get(id);
        AuthorDetailsDto expectedResponseBody = authorMapper.toDetailsDto(foundAuthor);

        mockMvc.perform(get("/api/authors/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

    @Test
    void getAuthorById_givenNotExistingId_returns404() throws Exception {
        Long id = 9999L;

        mockMvc.perform(get("/api/authors/{id}", id))
            .andExpect(status().isNotFound());
    }

    // createAuthor

    @Test
    void createAuthor_givenValidRequest_returnsCreatedWith201() throws Exception {
        AuthorRequestDto requestBody = new AuthorRequestDto("John", "Smith");

        String jsonResponse = mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andReturn()
            .getResponse()
            .getContentAsString();

        AuthorDetailsDto response = objectMapper.readValue(jsonResponse, AuthorDetailsDto.class);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getLastName()).isEqualTo("Smith");

        Author saved = authorRepository.findById(response.getId()).orElseThrow();
        assertThat(saved.getFirstName()).isEqualTo("John");
        assertThat(saved.getLastName()).isEqualTo("Smith");
    }

    @Test
    void createAuthor_givenEmptyRequest_returns400() throws Exception {
        mockMvc.perform(post("/api/authors"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createAuthor_givenNotValidRequest_returns400() throws Exception {
        AuthorRequestDto requestBody = new AuthorRequestDto(null, null);

        mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createAuthor_givenExistingAuthor_returns200() throws Exception {
        Author existingAuthor = AUTHORS.get(1L);
        AuthorRequestDto requestBody = new AuthorRequestDto(existingAuthor.getFirstName(), existingAuthor.getLastName());

        String jsonResponse = mockMvc.perform(post("/api/authors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andReturn()
            .getResponse()
            .getContentAsString();

        AuthorDetailsDto response = objectMapper.readValue(jsonResponse, AuthorDetailsDto.class);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getFirstName()).isEqualTo(existingAuthor.getFirstName());
        assertThat(response.getLastName()).isEqualTo(existingAuthor.getLastName());

        Author saved = authorRepository.findById(response.getId()).orElseThrow();
        assertThat(saved.getFirstName()).isEqualTo(existingAuthor.getFirstName());
        assertThat(saved.getLastName()).isEqualTo(existingAuthor.getLastName());
    }

    // searchAuthors

    @Test
    void searchAuthors_givenNotValidRequest_returns400() throws Exception {
        mockMvc.perform(post("/api/authors/_list")
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchAuthors_givenValidRequestWithFilterValueAndDefaultPagination_returnsAuthorsWith200() throws Exception {
        int expectedTotalElements = 5;
        AuthorFilter filter = new AuthorFilter(
            "s",
            "s",
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        testSearchAuthors(filter, expectedTotalElements, Comparator.comparingLong(AuthorInfoDto::getId));
    }

    @Test
    void searchAuthors_givenValidRequestWithFilterValueAndCustomPagination_returnsAuthorsWith200() throws Exception {
        int expectedTotalElements = 5;
        AuthorFilter filter = new AuthorFilter(
            "s",
            "s",
            2,
            3,
            "id,asc",
            null,
            null,
            null,
            null
        );

        testSearchAuthors(filter, expectedTotalElements, Comparator.comparingLong(AuthorInfoDto::getId));
    }

    @Test
    void searchAuthors_givenSortParamByMissingField_returnsAuthorsWith400() throws Exception {
        String invalidSort = "field,asc";
        mockMvc.perform(post("/api/authors/_list")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"sort\":\"%s\"}".formatted(invalidSort)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchAuthors_givenSortParamByMissingDirection_returnsAuthorsWith400() throws Exception {
        String invalidSort = "id,direction";
        mockMvc.perform(post("/api/authors/_list")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sort\":\"%s\"}".formatted(invalidSort)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchAuthors_givenSortParamWithIgnoreCaseValue_returnsAuthorsWith200() throws Exception {
        int expectedTotalElements = 5;
        AuthorFilter filter = new AuthorFilter(
            "s",
            "s",
            0,
            5,
            "Id,aSc",
            null,
            null,
            null,
            null
        );

        testSearchAuthors(filter, expectedTotalElements, Comparator.comparingLong(AuthorInfoDto::getId));
    }

    // updateAuthorById

    @Test
    void updateAuthorById_givenExistingIdAndValidRequest_returnsUpdatedAuthorWith200() throws Exception {
        Long id = 1L;
        AuthorRequestDto requestBody = new AuthorRequestDto("updatedFirstName", "updatedLastName");

        mockMvc.perform(put("/api/authors/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.firstName").value(requestBody.firstName()))
            .andExpect(jsonPath("$.lastName").value(requestBody.lastName()))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void updateAuthorById_givenNotExistingIdAndValidRequest_returns404() throws Exception {
        Long id = 9999L;
        AuthorRequestDto requestBody = new AuthorRequestDto("updatedFirstName", "updatedLastName");

        mockMvc.perform(put("/api/authors/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateAuthorById_givenExistingIdAndEmptyRequest_returns400() throws Exception {
        Long id = 1L;

        mockMvc.perform(put("/api/authors/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateAuthorById_givenExistingIdAndInvalidRequest_returns400() throws Exception {
        Long id = 1L;

        mockMvc.perform(put("/api/authors/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    // deleteAuthorById

    @Test
    void deleteAuthorById_givenExistingId_returns204() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/api/authors/{id}", id))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteAuthorById_givenNotExistingId_returns404() throws Exception {
        Long id = 9999L;

        mockMvc.perform(delete("/api/authors/{id}", id))
            .andExpect(status().isNotFound());
    }

    void testSearchAuthors(AuthorFilter filter, int expectedTotalElements, Comparator<AuthorInfoDto> comparator) throws Exception {
        int page = filter.page() != null ? filter.page() : 0;
        int size = filter.size() != null ? filter.size() : 10;
        int expectedTotalPages = (int)Math.ceil((double) expectedTotalElements / size);

        List<AuthorInfoDto> expectedAuthors = AUTHORS.values()
            .stream()
            .filter(x -> x.getFirstName().toLowerCase().contains(filter.firstName().toLowerCase()))
            .filter(x -> x.getLastName().toLowerCase().contains(filter.lastName().toLowerCase()))
            .skip((long) size * page)
            .limit(size)
            .map(x -> new AuthorInfoDto(x.getId(), x.getFirstName(), x.getLastName()))
            .sorted(comparator)
            .toList();

        PageDto<AuthorInfoDto> expectedResponseBody = new PageDto<>(
            expectedAuthors,
            page,
            size,
            expectedTotalElements,
            expectedTotalPages
        );

        mockMvc.perform(post("/api/authors/_list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

}