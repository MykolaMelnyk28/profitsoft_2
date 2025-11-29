package com.melnyk.profitsoft_2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDetailsDto {

    private Long id;
    private String title;
    private String description;
    private AuthorInfoDto author;
    private Integer yearPublished;
    private Integer pages;

    @Builder.Default
    private List<GenreInfoDto> genres = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
