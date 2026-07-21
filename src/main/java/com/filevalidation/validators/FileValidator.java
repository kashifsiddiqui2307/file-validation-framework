package com.filevalidation.validators;

import com.filevalidation.models.FileValidationContext;
import com.filevalidation.models.ValidationResult;

/**
 * Interface for file validators - pluggable architecture
 */
public interface FileValidator {
    /**
     * Validate file and return validation result
     */
    ValidationResult validate(FileValidationContext context);

    /**
     * Get validator name
     */
    String getName();

    /**
     * Check if validator supports given file type
     */
    boolean supports(String fileType);
}
