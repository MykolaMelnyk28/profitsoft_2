package com.melnyk.profitsoft_2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthorInfoDto implements InfoDto {

    private Long id;
    private String firstName;
    private String lastName;

}
