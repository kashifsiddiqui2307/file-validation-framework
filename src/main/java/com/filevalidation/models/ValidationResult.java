package com.filevalidation.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Model for file validation result
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationResult {
    private String fileName;
    private String validationType;
    private boolean passed;
    private long executionTimeMs;
    private String errorMessage;
    private Map<String, Object> details;
    private List<String> failureDetails;
    private int totalRecords;
    private int totalExpectedRecords;
}
