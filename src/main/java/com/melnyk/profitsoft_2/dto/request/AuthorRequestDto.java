package com.melnyk.profitsoft_2.dto.request;

import com.melnyk.profitsoft_2.validaton.Groups;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthorRequestDto(
    @NotNull(message = "firstName is required", groups =  {Groups.OnCreate.class, Groups.OnUpdate.class})
    @NotBlank(message = "firstName is required", groups = {Groups.OnCreate.class, Groups.OnUpdate.class})
    String firstName,
    @NotNull(message = "lastName is required", groups =  {Groups.OnCreate.class, Groups.OnUpdate.class})
    @NotBlank(message = "lastName is required", groups = {Groups.OnCreate.class, Groups.OnUpdate.class})
    String lastName
) { }
