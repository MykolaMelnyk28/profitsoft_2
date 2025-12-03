package com.melnyk.profitsoft_2.dto.request.filter;

import java.time.Instant;

public interface UpdatedFilter {

    Instant startUpdatedAt();
    Instant endUpdatedAt();

}
