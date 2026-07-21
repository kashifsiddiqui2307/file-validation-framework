package com.filevalidation.validators;

import com.filevalidation.models.FileValidationContext;
import com.filevalidation.models.NotNullFailure;
import com.filevalidation.models.ValidationResult;
import com.filevalidation.utils.CSVUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Validator for not-null validation
 * Validates critical columns don't have empty values ('' - nothing between delimiters)
 */
public class NotNullValidator implements FileValidator {
    private static final Logger logger = LogManager.getLogger(NotNullValidator.class);

    @Override
    public ValidationResult validate(FileValidationContext context) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting not-null validation for file: {}", context.getFileName());

        ValidationResult result = ValidationResult.builder()
                .fileName(context.getFileName())
                .validationType("Not-Null Validation")
                .passed(true)
                .details(new HashMap<>())
                .failureDetails(new ArrayList<>())
                .build();

        try {
            List<String[]> records = context.getParsedData();
            if (records == null || records.isEmpty()) {
                logger.warn("No parsed data available for not-null validation");
                result.setErrorMessage("No data available for validation");
                result.setPassed(false);
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            List<String> notNullColumns = context.getFileConfig().getNotNullColumns();
            if (notNullColumns == null || notNullColumns.isEmpty()) {
                logger.info("No not-null columns configured, skipping validation");
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            // Get headers and column indices
            List<String> headers = CSVUtil.getHeaders(records);
            Map<String, Integer> columnIndices = CSVUtil.getColumnIndices(headers, notNullColumns);

            if (columnIndices.isEmpty()) {
                result.setPassed(false);
                result.setErrorMessage("Configured not-null columns not found in file");
                result.getFailureDetails().add(String.format("Columns not found: %s", notNullColumns));
                logger.warn("Not-null columns not found in file headers");
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            // Check data rows for empty values
            List<NotNullFailure> failures = new ArrayList<>();
            int startIndex = context.getFileConfig().isHasHeader() ? 1 : 0;

            for (String columnName : columnIndices.keySet()) {
                int columnIndex = columnIndices.get(columnName);
                List<Integer> failureRows = new ArrayList<>();

                for (int i = startIndex; i < records.size(); i++) {
                    String[] record = records.get(i);
                    
                    // Skip trailer record
                    if (record.length > 0 && record[0].startsWith("TRL")) {
                        continue;
                    }

                    if (columnIndex < record.length) {
                        String value = record[columnIndex].trim();
                        
                        // Check for empty value ('' - nothing between delimiters)
                        // Accept: null, NULL, blank space, any other value
                        // Reject: empty string after trim
                        if (value.isEmpty()) {
                            failureRows.add(i + 1); // 1-indexed row number
                        }
                    }
                }

                if (!failureRows.isEmpty()) {
                    result.setPassed(false);
                    NotNullFailure failure = NotNullFailure.builder()
                            .columnName(columnName)
                            .rowNumbers(failureRows)
                            .build();
                    failures.add(failure);
                    
                    result.getFailureDetails().add(String.format("Column: %s, Row Numbers: %s", 
                            columnName, failureRows));
                    logger.warn("Not-null validation failed for column: {} at rows: {}", columnName, failureRows);
                }
            }

            if (!result.isPassed()) {
                result.getDetails().put("failures", failures);
                result.setErrorMessage("Not-null validation failed for " + failures.size() + " columns");
            } else {
                logger.info("Not-null validation passed successfully");
            }

        } catch (Exception e) {
            logger.error("Error during not-null validation", e);
            result.setPassed(false);
            result.setErrorMessage(e.getMessage());
        }

        result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        logger.info("Not-null validation completed in {} ms", result.getExecutionTimeMs());
        return result;
    }

    @Override
    public String getName() {
        return "Not-Null Validator";
    }

    @Override
    public boolean supports(String fileType) {
        return "CSV".equalsIgnoreCase(fileType);
    }
}
