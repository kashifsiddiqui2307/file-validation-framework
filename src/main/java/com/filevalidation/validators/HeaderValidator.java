package com.filevalidation.validators;

import com.filevalidation.models.FileValidationContext;
import com.filevalidation.models.ValidationResult;
import com.filevalidation.utils.CSVUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Validator for header validation
 */
public class HeaderValidator implements FileValidator {
    private static final Logger logger = LogManager.getLogger(HeaderValidator.class);

    @Override
    public ValidationResult validate(FileValidationContext context) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting header validation for file: {}", context.getFileName());

        ValidationResult result = ValidationResult.builder()
                .fileName(context.getFileName())
                .validationType("Header Validation")
                .passed(true)
                .details(new HashMap<>())
                .failureDetails(new ArrayList<>())
                .build();

        try {
            List<String[]> records = context.getParsedData();
            if (records == null || records.isEmpty()) {
                logger.warn("No parsed data available for header validation");
                result.setErrorMessage("No data available for validation");
                result.setPassed(false);
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            List<String> actualHeaders = CSVUtil.getHeaders(records);
            List<String> expectedHeaders = context.getFileConfig().getExpectedHeaders();

            if (expectedHeaders == null || expectedHeaders.isEmpty()) {
                logger.debug("No expected headers configured, skipping validation");
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            // Find missing and additional headers
            Set<String> expectedSet = new HashSet<>(expectedHeaders);
            Set<String> actualSet = new HashSet<>(actualHeaders);

            Set<String> missingHeaders = new HashSet<>(expectedSet);
            missingHeaders.removeAll(actualSet);

            Set<String> additionalHeaders = new HashSet<>(actualSet);
            additionalHeaders.removeAll(expectedSet);

            if (!missingHeaders.isEmpty() || !additionalHeaders.isEmpty()) {
                result.setPassed(false);
                if (!missingHeaders.isEmpty()) {
                    result.getFailureDetails().add("Missing Headers: " + missingHeaders);
                }
                if (!additionalHeaders.isEmpty()) {
                    result.getFailureDetails().add("Additional Headers: " + additionalHeaders);
                }
                logger.warn("Header validation failed");
            } else {
                logger.info("Header validation passed");
            }

        } catch (Exception e) {
            logger.error("Error during header validation", e);
            result.setPassed(false);
            result.setErrorMessage(e.getMessage());
        }

        result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        logger.info("Header validation completed in {} ms", result.getExecutionTimeMs());
        return result;
    }

    @Override
    public String getName() {
        return "Header Validator";
    }

    @Override
    public boolean supports(String fileType) {
        return "CSV".equalsIgnoreCase(fileType);
    }
}
