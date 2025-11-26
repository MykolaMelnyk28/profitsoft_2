package com.melnyk.profitsoft_2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "books",
    uniqueConstraints = @UniqueConstraint(name = "uq_books_title_author_id", columnNames = { "title", "author_id" })
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "books_seq")
    @SequenceGenerator(
        name = "books_seq",
        sequenceName = "books_id_seq",
        allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne()
    @JoinColumn(name = "author_id")
    private Author author;

    @Column(name = "year_published", nullable = false)
    private Integer yearPublished;

    @Column(nullable = false)
    private Integer pages;

    @ManyToMany()
    @JoinTable(
        name = "books_genres",
        joinColumns = @JoinColumn(name = "book_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();

}
