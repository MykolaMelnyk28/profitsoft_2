package com.melnyk.profitsoft_2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenreDetailsDto {

    private Long id;
    private String name;
    private Instant createdAt;
    private Instant updatedAt;

}
