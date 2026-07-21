package com.filevalidation.validators;

import com.filevalidation.models.FileValidationContext;
import com.filevalidation.models.ValidationResult;
import com.filevalidation.utils.DateUtil;
import com.filevalidation.utils.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Validator for date format validation
 * Validates YYYYMMDDHHMMSS format in ZIP file names
 */
public class DateFormatValidator implements FileValidator {
    private static final Logger logger = LogManager.getLogger(DateFormatValidator.class);

    @Override
    public ValidationResult validate(FileValidationContext context) {
        long startTime = System.currentTimeMillis();
        String fileName = context.getFileName();
        logger.info("Starting date format validation for file: {}", fileName);

        ValidationResult result = ValidationResult.builder()
                .fileName(fileName)
                .validationType("Date Format Validation")
                .passed(true)
                .details(new HashMap<>())
                .failureDetails(new ArrayList<>())
                .build();

        try {
            // Extract date from file name
            String dateString = DateUtil.extractDateFromFileName(fileName);
            if (dateString == null) {
                result.setPassed(false);
                result.setErrorMessage("Unable to extract date from file name");
                result.getFailureDetails().add(String.format("File: %s - Cannot extract YYYYMMDDHHMMSS format", fileName));
                logger.warn("Unable to extract date from file name: {}", fileName);
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            // Validate date format
            boolean isValid = DateUtil.validateDateFormat(dateString);
            if (!isValid) {
                result.setPassed(false);
                result.setErrorMessage(String.format("Invalid date format: %s", dateString));
                result.getFailureDetails().add(String.format("File: %s - Date: %s - Invalid format", fileName, dateString));
                logger.warn("Invalid date format in file: {}", fileName);
            } else {
                logger.info("Date format validation passed for file: {} - Date: {}", fileName, dateString);
            }

        } catch (Exception e) {
            logger.error("Error during date format validation", e);
            result.setPassed(false);
            result.setErrorMessage(e.getMessage());
        }

        result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        logger.info("Date format validation completed in {} ms", result.getExecutionTimeMs());
        return result;
    }

    @Override
    public String getName() {
        return "Date Format Validator";
    }

    @Override
    public boolean supports(String fileType) {
        return "ZIP".equalsIgnoreCase(fileType);
    }
}
