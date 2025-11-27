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

import java.util.List;

public final class DataUtil {
    private DataUtil() {}

    public static List<Genre> saveDefaultGenres(ObjectMapper mapper, GenreRepository repo) {
        return saveList(mapper, "genres.json", new TypeReference<List<Genre>>() {}, repo);
    }

    public static List<Book> saveDefaultBooks(ObjectMapper mapper, BookRepository repo) {
        return saveList(mapper, "books.json", new TypeReference<List<Book>>() {}, repo);
    }

    public static List<Author> saveDefaultAuthors(ObjectMapper mapper, AuthorRepository repo) {
        return saveList(mapper, "authors.json", new TypeReference<List<Author>>() {}, repo);
    }

    private static <T> List<T> loadList(
        ObjectMapper mapper,
        String fileName,
        TypeReference<List<T>> typeRef
    ) {
        return mapper.readValue(TestConstants.DEFAULT_DATA_DIR.resolve(fileName), typeRef);
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