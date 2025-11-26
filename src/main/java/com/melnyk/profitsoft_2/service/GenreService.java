package com.melnyk.profitsoft_2.service;

import com.melnyk.profitsoft_2.dto.request.GenreFilter;
import com.melnyk.profitsoft_2.dto.request.GenreRequestDto;
import com.melnyk.profitsoft_2.dto.response.GenreDto;
import com.melnyk.profitsoft_2.exception.ResourceAlreadyExistsException;
import com.melnyk.profitsoft_2.exception.ResourceNotFoundException;

import java.util.List;

public interface GenreService {

    GenreDto create(GenreRequestDto body) throws ResourceAlreadyExistsException;

    GenreDto getById(Long id) throws ResourceNotFoundException;

    List<GenreDto> search(GenreFilter filter);

    GenreDto updateById(Long id, GenreRequestDto body) throws ResourceNotFoundException, ResourceAlreadyExistsException;

    void deleteById(Long id) throws ResourceNotFoundException;

}
