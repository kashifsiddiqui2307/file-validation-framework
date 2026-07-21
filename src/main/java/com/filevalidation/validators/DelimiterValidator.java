package com.filevalidation.validators;

import com.filevalidation.models.*;
import com.filevalidation.utils.CSVUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Validator for delimiter validation
 * Checks all record data lines for delimiter consistency
 */
public class DelimiterValidator implements FileValidator {
    private static final Logger logger = LogManager.getLogger(DelimiterValidator.class);

    @Override
    public ValidationResult validate(FileValidationContext context) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting delimiter validation for file: {}", context.getFileName());

        ValidationResult result = ValidationResult.builder()
                .fileName(context.getFileName())
                .validationType("Delimiter Validation")
                .passed(true)
                .details(new HashMap<>())
                .failureDetails(new ArrayList<>())
                .build();

        try {
            List<String[]> records = context.getParsedData();
            if (records == null || records.isEmpty()) {
                logger.warn("No parsed data available for delimiter validation");
                result.setErrorMessage("No data available for validation");
                result.setPassed(false);
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            String delimiter = context.getFileConfig().getDelimiter();
            List<DelimiterFailure> failures = new ArrayList<>();
            
            // Skip header if present
            int startIndex = context.getFileConfig().isHasHeader() ? 1 : 0;
            int expectedFieldCount = records.get(0).length;

            for (int i = startIndex; i < records.size(); i++) {
                String[] record = records.get(i);
                
                // Skip trailer record if present
                if (record.length > 0 && record[0].startsWith("TRL")) {
                    continue;
                }

                // Check if field count matches expected
                if (record.length != expectedFieldCount) {
                    result.setPassed(false);
                    
                    // Find which columns are problematic
                    for (int j = 0; j < Math.max(record.length, expectedFieldCount); j++) {
                        String columnName = j < records.get(0).length ? records.get(0)[j] : "Column_" + (j + 1);
                        
                        DelimiterFailure failure = failures.stream()
                                .filter(f -> f.getColumnName().equals(columnName))
                                .findFirst()
                                .orElse(new DelimiterFailure(columnName, new ArrayList<>()));
                        
                        failure.getRowNumbers().add(i + 1); // 1-indexed row number
                        if (failures.stream().noneMatch(f -> f.getColumnName().equals(columnName))) {
                            failures.add(failure);
                        }
                    }
                }
            }

            if (!result.isPassed()) {
                result.setErrorMessage("Delimiter validation failed for " + failures.size() + " columns");
                result.getDetails().put("failures", failures);
                for (DelimiterFailure failure : failures) {
                    result.getFailureDetails().add(String.format("Column: %s, Row Numbers: %s", 
                            failure.getColumnName(), failure.getRowNumbers()));
                }
                logger.warn("Delimiter validation failed for {} columns", failures.size());
            } else {
                logger.info("Delimiter validation passed successfully");
            }

        } catch (Exception e) {
            logger.error("Error during delimiter validation", e);
            result.setPassed(false);
            result.setErrorMessage(e.getMessage());
        }

        result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        logger.info("Delimiter validation completed in {} ms", result.getExecutionTimeMs());
        return result;
    }

    @Override
    public String getName() {
        return "Delimiter Validator";
    }

    @Override
    public boolean supports(String fileType) {
        return "CSV".equalsIgnoreCase(fileType);
    }
}
