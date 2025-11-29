package com.melnyk.profitsoft_2.mapper;

import com.melnyk.profitsoft_2.dto.request.AuthorRequestDto;
import com.melnyk.profitsoft_2.dto.response.AuthorDetailsDto;
import com.melnyk.profitsoft_2.dto.response.AuthorInfoDto;
import com.melnyk.profitsoft_2.entity.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper
public interface AuthorMapper {

    AuthorDetailsDto toDetailsDto(Author author);
    AuthorInfoDto toInfoDto(Author author);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "books", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    Author toEntity(AuthorRequestDto dto);

}
