package com.melnyk.profitsoft_2.dto.request;

import com.melnyk.profitsoft_2.validaton.Groups;
import jakarta.validation.constraints.*;

import java.util.Set;

public record BookRequestDto(
    @NotNull(message = "title is required", groups =  {Groups.OnCreate.class, Groups.OnUpdate.class})
    @NotBlank(message = "title is required", groups = {Groups.OnCreate.class, Groups.OnUpdate.class})
    String title,

    String description,

    @NotNull(message = "authorId is required", groups =  {Groups.OnCreate.class, Groups.OnUpdate.class})
    @Positive(message = "authorId must be positive", groups = {Groups.OnCreate.class, Groups.OnUpdate.class})
    Long authorId,

    @NotNull(message = "yearPublished is required", groups =  {Groups.OnCreate.class, Groups.OnUpdate.class})
    @Min(value = 1450, message = "yearPublished must greater or equals than 1450", groups = {Groups.OnCreate.class, Groups.OnUpdate.class})
    Integer yearPublished,

    @NotNull(message = "pages is required", groups =  {Groups.OnCreate.class, Groups.OnUpdate.class})
    @Positive(message = "pages must be positive", groups = {Groups.OnCreate.class, Groups.OnUpdate.class})
    Integer pages,

    @NotNull(message = "genreIds is required", groups =  {Groups.OnCreate.class, Groups.OnUpdate.class})
    @NotEmpty(message = "genreIds must not be empty", groups =  {Groups.OnCreate.class, Groups.OnUpdate.class})
    Set<@Positive(message = "genre id must be positive", groups = {Groups.OnCreate.class, Groups.OnUpdate.class}) Long> genreIds
) { }
