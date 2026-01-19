package com.melnyk.profitsoft_2.util;

import com.melnyk.profitsoft_2.dto.response.BookDetailsDto;
import com.melnyk.profitsoft_2.event.BookEvent;

import java.time.Instant;
import java.util.Map;

public final class BookEventFactory {
    private BookEventFactory() {}

    public static BookEvent createBookCreatedEvent(Instant occurredAt, BookDetailsDto dto) {
        return new BookEvent(
            BookEvent.CREATE_TYPE,
            occurredAt,
            dto,
            Map.of()
        );
    }

}
