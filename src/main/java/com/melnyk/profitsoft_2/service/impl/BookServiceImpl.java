package com.melnyk.profitsoft_2.service.impl;

import com.melnyk.profitsoft_2.repository.BookRepository;
import com.melnyk.profitsoft_2.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

}
