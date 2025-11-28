package com.melnyk.profitsoft_2.service;

import com.melnyk.profitsoft_2.dto.request.AuthorRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.AuthorFilter;
import com.melnyk.profitsoft_2.dto.response.AuthorDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.exception.ResourceAlreadyExistsException;
import com.melnyk.profitsoft_2.exception.ResourceNotFoundException;

public interface AuthorService {

    AuthorDto create(AuthorRequestDto body) throws ResourceAlreadyExistsException;

    AuthorDto getById(Long id) throws ResourceNotFoundException;

    PageDto<AuthorDto> search(AuthorFilter filter);

    AuthorDto updateById(Long id, AuthorRequestDto body) throws ResourceNotFoundException, ResourceAlreadyExistsException;

    void deleteById(Long id) throws ResourceNotFoundException;

}
