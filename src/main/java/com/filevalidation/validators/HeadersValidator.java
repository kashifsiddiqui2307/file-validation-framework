package com.filevalidation.validators;

import com.filevalidation.models.FileValidationContext;
import com.filevalidation.models.ValidationResult;
import com.filevalidation.utils.CSVUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Validator for expected headers validation
 * Validates CSV headers match configuration
 */
public class HeadersValidator implements FileValidator {
    private static final Logger logger = LogManager.getLogger(HeadersValidator.class);

    @Override
    public ValidationResult validate(FileValidationContext context) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting headers validation for file: {}", context.getFileName());

        ValidationResult result = ValidationResult.builder()
                .fileName(context.getFileName())
                .validationType("Headers Validation")
                .passed(true)
                .details(new HashMap<>())
                .failureDetails(new ArrayList<>())
                .build();

        try {
            List<String[]> records = context.getParsedData();
            if (records == null || records.isEmpty()) {
                logger.warn("No parsed data available for headers validation");
                result.setErrorMessage("No data available for validation");
                result.setPassed(false);
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            List<String> expectedHeaders = context.getFileConfig().getExpectedHeaders();
            if (expectedHeaders == null || expectedHeaders.isEmpty()) {
                logger.info("No expected headers configured, skipping validation");
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            // Get actual headers from first row
            List<String> actualHeaders = CSVUtil.getHeaders(records);
            if (actualHeaders.isEmpty()) {
                result.setPassed(false);
                result.setErrorMessage("No headers found in CSV file");
                result.getFailureDetails().add("CSV file has no header row");
                logger.warn("No headers found in CSV file");
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            // Compare headers
            Set<String> expectedSet = new HashSet<>(expectedHeaders);
            Set<String> actualSet = new HashSet<>(actualHeaders);

            Set<String> missingHeaders = new HashSet<>(expectedSet);
            missingHeaders.removeAll(actualSet);

            Set<String> additionalHeaders = new HashSet<>(actualSet);
            additionalHeaders.removeAll(expectedSet);

            if (!missingHeaders.isEmpty() || !additionalHeaders.isEmpty()) {
                result.setPassed(false);
                
                if (!missingHeaders.isEmpty()) {
                    result.getFailureDetails().add(String.format("Missing Headers: %s", missingHeaders));
                    logger.warn("Missing headers: {}", missingHeaders);
                }
                if (!additionalHeaders.isEmpty()) {
                    result.getFailureDetails().add(String.format("Additional Headers: %s", additionalHeaders));
                    logger.warn("Additional headers: {}", additionalHeaders);
                }
                
                result.setErrorMessage("Header mismatch detected");
            } else {
                logger.info("Headers validation passed successfully");
            }

        } catch (Exception e) {
            logger.error("Error during headers validation", e);
            result.setPassed(false);
            result.setErrorMessage(e.getMessage());
        }

        result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        logger.info("Headers validation completed in {} ms", result.getExecutionTimeMs());
        return result;
    }

    @Override
    public String getName() {
        return "Headers Validator";
    }

    @Override
    public boolean supports(String fileType) {
        return "CSV".equalsIgnoreCase(fileType);
    }
}
