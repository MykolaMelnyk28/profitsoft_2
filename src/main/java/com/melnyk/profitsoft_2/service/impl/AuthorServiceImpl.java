package com.melnyk.profitsoft_2.service.impl;

import com.melnyk.profitsoft_2.repository.AuthorRepository;
import com.melnyk.profitsoft_2.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;

}
