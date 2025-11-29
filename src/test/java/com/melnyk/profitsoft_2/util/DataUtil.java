package com.melnyk.profitsoft_2.util;

import com.melnyk.profitsoft_2.TestConstants;
import com.melnyk.profitsoft_2.entity.Author;
import com.melnyk.profitsoft_2.entity.Book;
import com.melnyk.profitsoft_2.entity.Genre;
import com.melnyk.profitsoft_2.repository.AuthorRepository;
import com.melnyk.profitsoft_2.repository.BookRepository;
import com.melnyk.profitsoft_2.repository.GenreRepository;
import org.springframework.data.repository.CrudRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class DataUtil {
    private DataUtil() {}

    public static List<Genre> saveDefaultGenres(ObjectMapper mapper, GenreRepository repo) {
        return saveList(mapper, "genres.json", new TypeReference<List<Genre>>() {}, repo);
    }

    public static List<Author> saveDefaultAuthors(ObjectMapper mapper, AuthorRepository repo) {
        return saveList(mapper, "authors.json", new TypeReference<List<Author>>() {}, repo);
    }

    public static List<Book> saveDefaultBooks(
        ObjectMapper mapper,
        BookRepository bookRepo,
        AuthorRepository authorRepo,
        GenreRepository genreRepo
    ) {
        List<Book> books = loadList(mapper, "books.json", new TypeReference<List<Book>>() {});

        // Load existing authors + genres from DB
        Map<String, Author> authorByFullName = authorRepo.findAll()
            .stream()
            .collect(Collectors.toMap(
                a -> a.getFirstName() + " " + a.getLastName(),
                a -> a
            ));

        Map<String, Genre> genreByName = genreRepo.findAll()
            .stream()
            .collect(Collectors.toMap(Genre::getName, g -> g));

        // Load existing (title, author_id) pairs to avoid duplicates
        Set<String> existingPairs = bookRepo.findAll().stream()
            .map(b -> b.getTitle() + "::" + b.getAuthor().getId())
            .collect(Collectors.toSet());

        List<Book> result = new ArrayList<>();

        for (Book b : books) {

            // 1. Map author
            Author oldAuthor = b.getAuthor();
            String fullName = oldAuthor.getFirstName() + " " + oldAuthor.getLastName();
            Author realAuthor = authorByFullName.get(fullName);
            b.setAuthor(realAuthor);

            // 2. Map genres
            Set<Genre> mappedGenres = b.getGenres().stream()
                .map(g -> genreByName.get(g.getName()))
                .collect(Collectors.toSet());
            b.setGenres(mappedGenres);

            // 3. Remove IDs and timestamps
            b.setId(null);
            b.setCreatedAt(null);
            b.setUpdatedAt(null);

            // 4. Ensure unique (title, author_id)
            String originalTitle = b.getTitle();
            String finalTitle = originalTitle;
            int counter = 1;

            String key = finalTitle + "::" + realAuthor.getId();
            while (existingPairs.contains(key)) {
                finalTitle = originalTitle + " (" + counter++ + ")";
                key = finalTitle + "::" + realAuthor.getId();
            }

            // update title if changed
            b.setTitle(finalTitle);
            existingPairs.add(key);

            result.add(b);
        }

        return bookRepo.saveAll(result);
    }


    // -------------------------------------------------------------
    // Internal load/save helpers
    // -------------------------------------------------------------
    private static <T> List<T> loadList(
        ObjectMapper mapper,
        String fileName,
        TypeReference<List<T>> typeRef
    ) {
        try {
            return mapper.readValue(TestConstants.DEFAULT_DATA_DIR.resolve(fileName), typeRef);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load JSON: " + fileName, e);
        }
    }

    private static <T> List<T> saveList(
        ObjectMapper mapper,
        String fileName,
        TypeReference<List<T>> typeRef,
        CrudRepository<T, ?> repository
    ) {
        List<T> list = loadList(mapper, fileName, typeRef);
        return (List<T>) repository.saveAll(list);
    }
}