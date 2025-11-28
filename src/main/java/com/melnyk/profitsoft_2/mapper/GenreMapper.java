package com.melnyk.profitsoft_2.mapper;

import com.melnyk.profitsoft_2.dto.request.GenreRequestDto;
import com.melnyk.profitsoft_2.dto.response.GenreDto;
import com.melnyk.profitsoft_2.entity.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface GenreMapper {

    GenreDto toDto(Genre genre);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "books", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    Genre toEntity(GenreRequestDto body);

}
