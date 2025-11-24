package com.melnyk.profitsoft_2.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "genres",
    uniqueConstraints = @UniqueConstraint(name = "uq_genres_name", columnNames = "name")
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genres_seq")
    @SequenceGenerator(
        name = "genres_seq",
        sequenceName = "genres_id_seq",
        allocationSize = 1
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToMany(
        mappedBy = "genres",
        cascade = CascadeType.ALL
    )
    private List<Book> books = new ArrayList<>();

}
