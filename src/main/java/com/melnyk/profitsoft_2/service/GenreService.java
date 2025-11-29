package com.melnyk.profitsoft_2.service;

import com.melnyk.profitsoft_2.dto.request.GenreRequestDto;
import com.melnyk.profitsoft_2.dto.request.filter.impl.GenreFilter;
import com.melnyk.profitsoft_2.dto.response.GenreDetailsDto;
import com.melnyk.profitsoft_2.dto.response.GenreInfoDto;
import com.melnyk.profitsoft_2.dto.response.PageDto;
import com.melnyk.profitsoft_2.exception.ResourceAlreadyExistsException;
import com.melnyk.profitsoft_2.exception.ResourceNotFoundException;

public interface GenreService {

    GenreDetailsDto create(GenreRequestDto body) throws ResourceAlreadyExistsException;

    GenreDetailsDto getById(Long id) throws ResourceNotFoundException;

    PageDto<GenreInfoDto> search(GenreFilter filter);

    GenreDetailsDto updateById(Long id, GenreRequestDto body) throws ResourceNotFoundException, ResourceAlreadyExistsException;

    void deleteById(Long id) throws ResourceNotFoundException;

}
