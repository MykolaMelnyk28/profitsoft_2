package com.melnyk.profitsoft_2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponse {

    public record FailedItem(Object object, String reason) {}

    private int totalCount;
    private int createdCount;
    private int failedCount;

    @Builder.Default
    private List<FailedItem> failedItems = new ArrayList<>();

}
