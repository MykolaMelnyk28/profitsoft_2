package com.melnyk.profitsoft_2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorDetailsDto {

    private Long id;
    private String firstName;
    private String lastName;
    private Instant createdAt;
    private Instant updatedAt;

}
