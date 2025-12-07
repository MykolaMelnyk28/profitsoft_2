package com.melnyk.profitsoft_2.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * The DTO class that contains result of upload operation
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadResponse {

    /**
     * The class contains data about fail upload operation of object
     * @param object object
     * @param reason reason of fail
     */
    public record FailedItem(Object object, String reason) {}

    /**
     * Total count objects that provides to upload operation
     */
    private int totalCount;

    /**
     * Count of objects that were created
     */
    private int createdCount;

    /**
     * Count of objects that encountered an error while creating
     */
    private int failedCount;

    /**
     * List of {@link FailedItem}
     */
    @Builder.Default
    private List<FailedItem> failedItems = new ArrayList<>();

}
