package com.filevalidation.core;

import com.filevalidation.config.ConfigurationManager;
import com.filevalidation.models.*;
import com.filevalidation.utils.CSVUtil;
import com.filevalidation.utils.FileUtil;
import com.filevalidation.validators.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Core validation engine - orchestrates all validations
 */
public class ValidationEngine {
    private static final Logger logger = LogManager.getLogger(ValidationEngine.class);
    private ConfigurationManager configManager;
    private List<FileValidator> validators;
    private List<ValidationResult> validationResults;

    public ValidationEngine() {
        this.validators = new ArrayList<>();
        this.validationResults = new ArrayList<>();
        initializeValidators();
    }

    /**
     * Initialize all available validators
     */
    private void initializeValidators() {
        validators.add(new DelimiterValidator());
        validators.add(new TrailerValidator());
        validators.add(new FilePresenceValidator());
        validators.add(new DateFormatValidator());
        validators.add(new HeaderValidator());
        validators.add(new NotNullValidator());
        logger.info("Initialized {} validators", validators.size());
    }

    /**
     * Load configuration from file
     */
    public void loadConfiguration(String configFilePath) throws IOException {
        configManager = new ConfigurationManager();
        configManager.loadConfiguration(configFilePath);
        logger.info("Configuration loaded from: {}", configFilePath);
    }

    /**
     * Validate a single file
     */
    public ValidationResult validateFile(String filePath, FileConfig fileConfig) {
        logger.info("Starting validation for file: {}", filePath);
        validationResults.clear();

        try {
            // Create validation context
            FileValidationContext context = createValidationContext(filePath, fileConfig);

            // Execute applicable validators
            for (FileValidator validator : validators) {
                if (shouldRunValidator(validator, fileConfig)) {
                    logger.info("Running validator: {}", validator.getName());
                    ValidationResult result = validator.validate(context);
                    validationResults.add(result);
                }
            }

            logger.info("Validation completed for file: {}", filePath);
        } catch (Exception e) {
            logger.error("Error during validation of file: {}", filePath, e);
        }

        return aggregateResults();
    }

    /**
     * Validate directory
     */
    public ValidationResult validateDirectory(String directoryPath) {
        logger.info("Starting directory validation: {}", directoryPath);
        validationResults.clear();

        if (configManager == null || configManager.getValidationConfig() == null) {
            logger.error("Configuration not loaded. Call loadConfiguration() first.");
            return null;
        }

        ValidationConfig config = configManager.getValidationConfig();
        for (FileConfig fileConfig : config.getFiles()) {
            String filePath = Paths.get(directoryPath, fileConfig.getFileName()).toString();
            if (FileUtil.fileExists(filePath)) {
                validateFile(filePath, fileConfig);
            }
        }

        logger.info("Directory validation completed: {}", directoryPath);
        return aggregateResults();
    }

    /**
     * Create validation context from file and configuration
     */
    private FileValidationContext createValidationContext(String filePath, FileConfig fileConfig) throws IOException {
        String fileName = Paths.get(filePath).getFileName().toString();
        List<String> fileLines = FileUtil.readFileLines(filePath);
        String content = String.join("\n", fileLines);
        List<String[]> parsedData = CSVUtil.parseCSV(content, fileConfig.getDelimiter());

        return FileValidationContext.builder()
                .filePath(filePath)
                .fileName(fileName)
                .fileConfig(fileConfig)
                .fileType(fileConfig.getFileType())
                .fileLines(fileLines)
                .parsedData(parsedData)
                .build();
    }

    /**
     * Check if validator should run based on skipValidations configuration
     */
    private boolean shouldRunValidator(FileValidator validator, FileConfig fileConfig) {
        if (fileConfig.getSkipValidations() != null) {
            return !fileConfig.getSkipValidations().contains(validator.getName());
        }
        return validator.supports(fileConfig.getFileType());
    }

    /**
     * Aggregate all validation results
     */
    private ValidationResult aggregateResults() {
        boolean allPassed = validationResults.stream().allMatch(ValidationResult::isPassed);
        long totalTime = validationResults.stream().mapToLong(ValidationResult::getExecutionTimeMs).sum();
        int totalFailures = (int) validationResults.stream().filter(r -> !r.isPassed()).count();

        return ValidationResult.builder()
                .validationType("Aggregated Validation")
                .passed(allPassed)
                .executionTimeMs(totalTime)
                .errorMessage(totalFailures > 0 ? totalFailures + " validations failed" : null)
                .build();
    }

    /**
     * Get all validation results
     */
    public List<ValidationResult> getValidationResults() {
        return new ArrayList<>(validationResults);
    }

    /**
     * Get validation results summary
     */
    public String getResultsSummary() {
        long passed = validationResults.stream().filter(ValidationResult::isPassed).count();
        long failed = validationResults.stream().filter(r -> !r.isPassed()).count();
        long totalTime = validationResults.stream().mapToLong(ValidationResult::getExecutionTimeMs).sum();

        return String.format("\n=== VALIDATION SUMMARY ===\nTotal: %d | Passed: %d | Failed: %d | Time: %dms\n",
                validationResults.size(), passed, failed, totalTime);
    }
}
