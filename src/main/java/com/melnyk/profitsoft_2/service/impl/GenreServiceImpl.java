package com.melnyk.profitsoft_2.service.impl;

import com.melnyk.profitsoft_2.repository.GenreRepository;
import com.melnyk.profitsoft_2.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

}
