package com.melnyk.profitsoft_2.repository;

import com.melnyk.profitsoft_2.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {

}
