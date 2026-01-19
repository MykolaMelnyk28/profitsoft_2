package com.melnyk.profitsoft_2.event;

import com.melnyk.profitsoft_2.dto.response.BookDetailsDto;

import java.time.Instant;
import java.util.Map;

public record BookEvent(
    String type,
    Instant occurredAt,
    BookDetailsDto data,
    Map<String, Object> properties
) implements Event<BookDetailsDto> {
    public static final String CREATE_TYPE = "CREATE";
}