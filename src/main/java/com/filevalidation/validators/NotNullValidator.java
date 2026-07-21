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
                logger.debug("No not-null columns configured, skipping validation");
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            List<String> headers = CSVUtil.getHeaders(records);
            Map<String, Integer> columnIndices = CSVUtil.getColumnIndices(headers, notNullColumns);
            List<NotNullFailure> failures = new ArrayList<>();

            // Skip header row
            int startIndex = context.getFileConfig().isHasHeader() ? 1 : 0;
            for (int i = startIndex; i < records.size(); i++) {
                String[] record = records.get(i);
                
                // Skip trailer
                if (record.length > 0 && record[0].startsWith("TRL")) {
                    continue;
                }

                for (String columnName : notNullColumns) {
                    Integer columnIndex = columnIndices.get(columnName);
                    if (columnIndex != null && columnIndex < record.length) {
                        String value = record[columnIndex];
                        if (value == null || value.trim().isEmpty()) {
                            result.setPassed(false);
                            NotNullFailure failure = failures.stream()
                                    .filter(f -> f.getColumnName().equals(columnName))
                                    .findFirst()
                                    .orElse(new NotNullFailure(columnName, new ArrayList<>()));
                            failure.getRowNumbers().add(i + 1);
                            if (failures.stream().noneMatch(f -> f.getColumnName().equals(columnName))) {
                                failures.add(failure);
                            }
                        }
                    }
                }
            }

            if (!result.isPassed()) {
                result.getDetails().put("failures", failures);
                for (NotNullFailure failure : failures) {
                    result.getFailureDetails().add(String.format("Column: %s, Rows with null/empty values: %s",
                            failure.getColumnName(), failure.getRowNumbers()));
                }
                logger.warn("Not-null validation failed for {} columns", failures.size());
            } else {
                logger.info("Not-null validation passed");
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
