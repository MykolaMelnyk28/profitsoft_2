package com.melnyk.profitsoft_2.service;

import com.melnyk.profitsoft_2.dto.request.BookRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.BookFilter;
import com.melnyk.profitsoft_2.dto.response.BookDetailsDto;
import com.melnyk.profitsoft_2.dto.response.BookInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.entity.Book;
import com.melnyk.profitsoft_2.exception.ResourceAlreadyExistsException;
import com.melnyk.profitsoft_2.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface BookService {

    BookDetailsDto create(BookRequestDto body) throws ResourceAlreadyExistsException;

    BookDetailsDto getById(Long id) throws ResourceNotFoundException;

    Book getByIdOrThrow(Long id) throws ResourceNotFoundException;

    PageDto<BookInfoDto> search(BookFilter filter);

    BookDetailsDto updateById(Long id, BookRequestDto body) throws ResourceNotFoundException, ResourceAlreadyExistsException;

    void deleteById(Long id) throws ResourceNotFoundException;

    void generateReport(BookFilter filter, HttpServletResponse response) throws IOException;

}
