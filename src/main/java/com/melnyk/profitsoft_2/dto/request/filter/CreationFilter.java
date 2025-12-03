package com.melnyk.profitsoft_2.dto.request.filter;

import java.time.Instant;

public interface CreationFilter {

    Instant startCreatedAt();
    Instant endCreatedAt();

}
