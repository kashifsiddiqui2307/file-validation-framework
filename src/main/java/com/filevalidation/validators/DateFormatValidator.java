package com.filevalidation.validators;

import com.filevalidation.models.FileValidationContext;
import com.filevalidation.models.ValidationResult;
import com.filevalidation.utils.DateUtil;
import com.filevalidation.utils.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Validator for date format validation in filenames
 */
public class DateFormatValidator implements FileValidator {
    private static final Logger logger = LogManager.getLogger(DateFormatValidator.class);
    private static final String DATE_PATTERN = "YYYYMMDDHHMMSS";

    @Override
    public ValidationResult validate(FileValidationContext context) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting date format validation for file: {}", context.getFileName());

        ValidationResult result = ValidationResult.builder()
                .fileName(context.getFileName())
                .validationType("Date Format Validation")
                .passed(true)
                .details(new HashMap<>())
                .failureDetails(new ArrayList<>())
                .build();

        try {
            String dateString = DateUtil.extractDateFromFileName(context.getFileName());
            if (dateString == null) {
                logger.warn("Could not extract date from filename: {}", context.getFileName());
                result.setErrorMessage("Could not extract date from filename");
                result.setPassed(false);
                result.getFailureDetails().add("Filename does not contain YYYYMMDDHHMMSS format");
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            if (!DateUtil.validateDateFormat(dateString)) {
                result.setPassed(false);
                result.setErrorMessage("Invalid date format in filename: " + dateString);
                result.getFailureDetails().add("Expected format: " + DATE_PATTERN);
                result.getFailureDetails().add("Found: " + dateString);
                logger.warn("Date format validation failed for: {}", dateString);
            } else {
                result.getDetails().put("extractedDate", dateString);
                logger.info("Date format validation passed for: {}", dateString);
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
        return true; // All file types can have date formats
    }
}
