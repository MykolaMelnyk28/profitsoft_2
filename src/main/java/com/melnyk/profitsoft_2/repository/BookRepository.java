package com.melnyk.profitsoft_2.repository;

import com.melnyk.profitsoft_2.entity.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

    @EntityGraph(attributePaths = { "author", "genres" })
    Optional<Book> findByTitleAndAuthorId(String title, Long authorId);

    @EntityGraph(attributePaths = { "author", "genres" })
    Optional<Book> findById(Long id);

}
