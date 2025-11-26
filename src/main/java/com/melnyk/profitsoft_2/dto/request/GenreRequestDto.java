package com.melnyk.profitsoft_2.dto.request;

import com.melnyk.profitsoft_2.validaton.Groups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record GenreRequestDto(
    @NotNull(message = "name is required", groups =  Groups.OnCreate.class)
    @NotBlank(message = "name is required", groups = Groups.OnCreate.class)
    String name
) { }
