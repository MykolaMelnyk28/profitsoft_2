package com.melnyk.profitsoft_2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "authors_seq")
    @SequenceGenerator(
        name = "authors_seq",
        sequenceName = "authors_id_seq",
        allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @OneToMany(
        mappedBy = "author",
        orphanRemoval = true,
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL
    )
    private List<Book> books = new ArrayList<>();

}
