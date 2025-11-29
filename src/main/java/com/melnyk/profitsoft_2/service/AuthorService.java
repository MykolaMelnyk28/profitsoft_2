package com.melnyk.profitsoft_2.service;

import com.melnyk.profitsoft_2.dto.request.AuthorRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.AuthorFilter;
import com.melnyk.profitsoft_2.dto.response.AuthorDetailsDto;
import com.melnyk.profitsoft_2.dto.response.AuthorInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.exception.ResourceAlreadyExistsException;
import com.melnyk.profitsoft_2.exception.ResourceNotFoundException;

public interface AuthorService {

    AuthorDetailsDto create(AuthorRequestDto body) throws ResourceAlreadyExistsException;

    AuthorDetailsDto getById(Long id) throws ResourceNotFoundException;

    PageDto<AuthorInfoDto> search(AuthorFilter filter);

    AuthorDetailsDto updateById(Long id, AuthorRequestDto body) throws ResourceNotFoundException, ResourceAlreadyExistsException;

    void deleteById(Long id) throws ResourceNotFoundException;

}
