package com.melnyk.profitsoft_2.dto.request.filter;

import java.time.LocalDateTime;

public interface CreationFilter {

    LocalDateTime startCreatedAt();
    LocalDateTime endCreatedAt();

}
