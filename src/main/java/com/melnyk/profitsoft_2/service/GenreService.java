package com.melnyk.profitsoft_2.service;

import com.melnyk.profitsoft_2.dto.request.filter.impl.GenreFilter;
import com.melnyk.profitsoft_2.dto.request.GenreRequestDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.dto.response.GenreDto;
import com.melnyk.profitsoft_2.exception.ResourceAlreadyExistsException;
import com.melnyk.profitsoft_2.exception.ResourceNotFoundException;

public interface GenreService {

    GenreDto create(GenreRequestDto body) throws ResourceAlreadyExistsException;

    GenreDto getById(Long id) throws ResourceNotFoundException;

    PageDto<GenreDto> search(GenreFilter filter);

    GenreDto updateById(Long id, GenreRequestDto body) throws ResourceNotFoundException, ResourceAlreadyExistsException;

    void deleteById(Long id) throws ResourceNotFoundException;

}
