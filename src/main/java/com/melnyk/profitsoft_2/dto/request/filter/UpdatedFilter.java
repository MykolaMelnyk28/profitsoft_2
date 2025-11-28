package com.melnyk.profitsoft_2.dto.request.filter;

import java.time.LocalDateTime;

public interface UpdatedFilter {

    LocalDateTime startUpdatedAt();
    LocalDateTime endUpdatedAt();

}
