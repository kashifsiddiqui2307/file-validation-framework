package com.filevalidation.validators;

import com.filevalidation.models.FileValidationContext;
import com.filevalidation.models.ValidationResult;
import com.filevalidation.utils.CSVUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Validator for trailer record validation
 * Validates TRL record count matches actual data record count
 */
public class TrailerValidator implements FileValidator {
    private static final Logger logger = LogManager.getLogger(TrailerValidator.class);

    @Override
    public ValidationResult validate(FileValidationContext context) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting trailer validation for file: {}", context.getFileName());

        ValidationResult result = ValidationResult.builder()
                .fileName(context.getFileName())
                .validationType("Trailer Validation")
                .passed(true)
                .details(new HashMap<>())
                .failureDetails(new ArrayList<>())
                .build();

        try {
            List<String[]> records = context.getParsedData();
            if (records == null || records.isEmpty()) {
                logger.warn("No parsed data available for trailer validation");
                result.setErrorMessage("No data available for validation");
                result.setPassed(false);
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            // Get trailer record
            String[] trailerRecord = CSVUtil.getTrailerRecord(records);
            if (trailerRecord == null) {
                logger.warn("No trailer record (TRL) found in the file");
                result.setErrorMessage("No trailer record found");
                result.setPassed(false);
                result.getFailureDetails().add("Missing TRL (trailer) record");
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            // Extract count from trailer record (assuming format: TRL,<count>)
            int trailerCount = extractCountFromTrailer(trailerRecord);
            if (trailerCount < 0) {
                logger.warn("Invalid trailer record format");
                result.setErrorMessage("Invalid trailer record format");
                result.setPassed(false);
                result.getFailureDetails().add("Unable to extract count from TRL record");
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            // Count actual data records (excluding header and trailer)
            int actualCount = CSVUtil.countDataRecords(records);

            result.setTotalRecords(actualCount);
            result.setTotalExpectedRecords(trailerCount);

            if (actualCount != trailerCount) {
                result.setPassed(false);
                result.setErrorMessage(String.format("Record count mismatch: Expected %d, Found %d", trailerCount, actualCount));
                result.getFailureDetails().add(String.format("Expected records (from TRL): %d", trailerCount));
                result.getFailureDetails().add(String.format("Actual records: %d", actualCount));
                result.getFailureDetails().add(String.format("Difference: %d", Math.abs(trailerCount - actualCount)));
                logger.warn("Trailer validation failed: expected {} records, found {}", trailerCount, actualCount);
            } else {
                logger.info("Trailer validation passed: {} records match", actualCount);
            }

        } catch (Exception e) {
            logger.error("Error during trailer validation", e);
            result.setPassed(false);
            result.setErrorMessage(e.getMessage());
        }

        result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        logger.info("Trailer validation completed in {} ms", result.getExecutionTimeMs());
        return result;
    }

    /**
     * Extract count from trailer record
     * Format: TRL,<count> or similar
     */
    private int extractCountFromTrailer(String[] trailerRecord) {
        try {
            if (trailerRecord.length >= 2) {
                String countStr = trailerRecord[1].trim();
                return Integer.parseInt(countStr);
            }
        } catch (NumberFormatException e) {
            logger.warn("Error parsing count from trailer record", e);
        }
        return -1;
    }

    @Override
    public String getName() {
        return "Trailer Validator";
    }

    @Override
    public boolean supports(String fileType) {
        return "CSV".equalsIgnoreCase(fileType);
    }
}
