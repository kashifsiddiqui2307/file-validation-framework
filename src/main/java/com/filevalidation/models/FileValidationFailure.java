package com.filevalidation.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model to hold file validation failure details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileValidationFailure {
    private List<String> missingFiles;
    private List<String> additionalFiles;
    private int csvFileCount;
    private int actualFileCount;
}
