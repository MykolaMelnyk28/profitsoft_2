package com.melnyk.profitsoft_2.mapper;

import com.melnyk.profitsoft_2.dto.request.BookRequestDto;
import com.melnyk.profitsoft_2.dto.response.BookDetailsDto;
import com.melnyk.profitsoft_2.dto.response.BookInfoDto;
import com.melnyk.profitsoft_2.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(uses = {AuthorMapper.class, GenreMapper.class})
public interface BookMapper {

    BookDetailsDto toDetailsDto(Book entity);
    BookInfoDto toInfoDto(Book entity);

    @Mappings({
        @Mapping(target = "id", ignore = true),
        @Mapping(target = "author", ignore = true),
        @Mapping(target = "genres", ignore = true),
        @Mapping(target = "createdAt", ignore = true),
        @Mapping(target = "updatedAt", ignore = true)
    })
    Book toEntity(BookRequestDto dto);

}
