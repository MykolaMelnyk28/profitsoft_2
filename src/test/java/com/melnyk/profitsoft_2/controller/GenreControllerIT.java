package com.melnyk.profitsoft_2.controller;

import com.melnyk.profitsoft_2.Profitsoft2Application;
import com.melnyk.profitsoft_2.config.TestConfig;
import com.melnyk.profitsoft_2.dto.request.GenreRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.GenreFilter;
import com.melnyk.profitsoft_2.dto.response.GenreDetailsDto;
import com.melnyk.profitsoft_2.dto.response.GenreInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.entity.Genre;
import com.melnyk.profitsoft_2.mapper.GenreMapper;
import com.melnyk.profitsoft_2.repository.GenreRepository;
import com.melnyk.profitsoft_2.util.DataUtil;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureEmbeddedDatabase(
    provider = AutoConfigureEmbeddedDatabase.DatabaseProvider.DEFAULT,
    type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES,
    beanName = "genreDatasource"
)
@ActiveProfiles("test")
class GenreControllerIT {

    static final Map<Long, Genre> GENRES = new HashMap<>();

    @Autowired
    MockMvc mockMvc;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    GenreMapper genreMapper;

    Instant initializedTime;

    @BeforeAll
    @Transactional
    void beforeEach() {
        initializedTime = Instant.now();
        DataUtil.saveDefaultGenres(objectMapper, genreRepository).forEach(x -> GENRES.put(x.getId(), x));
    }

    // getGenreById

    @Test
    @Transactional(readOnly = true)
    void getGenreById_givenExistingId_returnsGenreWith200() throws Exception {
        Long id = 3L;
        Genre foundGenre = GENRES.get(id);
        GenreDetailsDto expectedResponseBody = genreMapper.toDetailsDto(foundGenre);

        String jsonResponse = mockMvc.perform(get("/api/genres/{id}", id))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andReturn()
            .getResponse()
            .getContentAsString();

        GenreDetailsDto responseBody = objectMapper.readValue(jsonResponse, GenreDetailsDto.class);

        assertThat(responseBody)
            .usingRecursiveComparison()
            .ignoringFields("createdAt", "updatedAt")
            .isEqualTo(expectedResponseBody);

        assertThat(responseBody.getCreatedAt()).isAfterOrEqualTo(initializedTime);
        assertThat(responseBody.getUpdatedAt()).isAfterOrEqualTo(initializedTime);
    }

    @Test
    @Transactional(readOnly = true)
    void getGenreById_givenNotExistingId_returns404() throws Exception {
        Long id = 9999L;

        mockMvc.perform(get("/api/genres/{id}", id))
            .andExpect(status().isNotFound());
    }

    // createGenre

    @Test
    @Transactional
    @Rollback
    void createGenre_givenValidRequest_returnsCreatedWith201() throws Exception {
        GenreRequestDto requestBody = new GenreRequestDto("newGenre");

        String jsonResponse = mockMvc.perform(post("/api/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(header().exists(HttpHeaders.LOCATION))
            .andReturn()
            .getResponse()
            .getContentAsString();

        GenreDetailsDto response = objectMapper.readValue(jsonResponse, GenreDetailsDto.class);

        assertThat(response.getId()).isNotNull();
        assertThat(response.getName()).isEqualTo("newGenre");

        Genre saved = genreRepository.findById(response.getId()).orElseThrow();
        assertThat(saved.getName()).isEqualTo("newGenre");
    }

    @Test
    void createGenre_givenEmptyRequest_returns400() throws Exception {
        mockMvc.perform(post("/api/genres"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void createGenre_givenNotValidRequest_returns400() throws Exception {
        GenreRequestDto requestBody = new GenreRequestDto(null);

        mockMvc.perform(post("/api/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @Rollback
    void createGenre_givenExistingGenre_returns409() throws Exception {
        String existingName = GENRES.get(1L).getName();
        GenreRequestDto requestBody = new GenreRequestDto(existingName);

        mockMvc.perform(post("/api/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isConflict());
    }

    // searchGenres

    @Test
    void searchGenres_givenNotValidRequest_returns400() throws Exception {
        mockMvc.perform(post("/api/genres/_list")
                .content(""))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional(readOnly = true)
    void searchGenres_givenValidRequestWithFilterValueAndDefaultPagination_returnsGenresWith200() throws Exception {
        int expectedTotalElements = 8;
        GenreFilter filter = new GenreFilter(
            "F",
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        testSearchGenres(filter, expectedTotalElements, Comparator.comparingLong(GenreInfoDto::getId));
    }

    @Test
    @Transactional(readOnly = true)
    void searchGenres_givenValidRequestWithFilterValueAndCustomPagination_returnsGenresWith200() throws Exception {
        int expectedTotalElements = 8;
        GenreFilter filter = new GenreFilter(
            "F",
            2,
            3,
            "id,asc",
            null,
            null,
            null,
            null
        );

        testSearchGenres(filter, expectedTotalElements, Comparator.comparingLong(GenreInfoDto::getId));
    }

    @Test
    void searchGenres_givenSortParamByMissingField_returnsGenresWith400() throws Exception {
        String invalidSort = "field,asc";
        mockMvc.perform(post("/api/genres/_list")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"sort\":\"%s\"}".formatted(invalidSort)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void searchGenres_givenSortParamByMissingDirection_returnsGenresWith400() throws Exception {
        String invalidSort = "id,direction";
        mockMvc.perform(post("/api/genres/_list")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"sort\":\"%s\"}".formatted(invalidSort)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional(readOnly = true)
    void searchGenres_givenSortParamWithIgnoreCaseValue_returnsGenresWith200() throws Exception {
        int expectedTotalElements = 8;
        GenreFilter filter = new GenreFilter(
            "f",
            0,
            5,
            "Id,aSc",
            null,
            null,
            null,
            null
        );

        testSearchGenres(filter, expectedTotalElements, Comparator.comparingLong(GenreInfoDto::getId));
    }

    // updateGenreById

    @Test
    @Transactional
    @Rollback
    void updateGenreById_givenExistingIdAndValidRequest_returnsUpdatedGenreWith200() throws Exception {
        Long id = 1L;
        GenreRequestDto requestBody = new GenreRequestDto("updatedGenre1");

        mockMvc.perform(put("/api/genres/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestBody)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(id))
            .andExpect(jsonPath("$.name").value(requestBody.name()))
            .andExpect(jsonPath("$.createdAt").exists())
            .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    @Transactional(readOnly = true)
    void updateGenreById_givenNotExistingIdAndValidRequest_returns404() throws Exception {
        Long id = 9999L;

        mockMvc.perform(put("/api/genres/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"newGenre\"}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateGenreById_givenExistingIdAndEmptyRequest_returns400() throws Exception {
        Long id = 1L;

        mockMvc.perform(put("/api/genres/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateGenreById_givenExistingIdAndInvalidRequest_returns400() throws Exception {
        Long id = 1L;

        mockMvc.perform(put("/api/genres/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    // deleteGenreById

    @Test
    @Transactional
    @Rollback
    void deleteGenreById_givenExistingId_returns204() throws Exception {
        Long id = 1L;

        mockMvc.perform(delete("/api/genres/{id}", id))
            .andExpect(status().isNoContent());
    }

    @Test
    @Transactional(readOnly = true)
    void deleteGenreById_givenNotExistingId_returns404() throws Exception {
        Long id = 9999L;

        mockMvc.perform(delete("/api/genres/{id}", id))
            .andExpect(status().isNotFound());
    }

    void testSearchGenres(GenreFilter filter, int expectedTotalElements, Comparator<GenreInfoDto> comparator) throws Exception {
        int page = filter.page() != null ? filter.page() : 0;
        int size = filter.size() != null ? filter.size() : 10;
        int expectedTotalPages = (int)Math.ceil((double) expectedTotalElements / size);

        List<GenreInfoDto> expectedGenres = GENRES.values()
            .stream()
            .filter(x -> x.getName().toLowerCase().contains(filter.name().toLowerCase()))
            .skip((long) size * page)
            .limit(size)
            .map(x -> new GenreInfoDto(x.getId(), x.getName()))
            .sorted(comparator)
            .toList();

        PageDto<GenreInfoDto> expectedResponseBody = new PageDto<>(
            expectedGenres,
            page,
            size,
            expectedTotalElements,
            expectedTotalPages
        );

        mockMvc.perform(post("/api/genres/_list")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(filter)))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().json(objectMapper.writeValueAsString(expectedResponseBody)));
    }

}