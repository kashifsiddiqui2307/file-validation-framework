package com.filevalidation.validators;

import com.filevalidation.models.*;
import com.filevalidation.utils.CSVUtil;
import com.filevalidation.utils.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Validator for file presence validation
 * Searches for .meta.zip file, extracts it, and validates file presence
 */
public class FilePresenceValidator implements FileValidator {
    private static final Logger logger = LogManager.getLogger(FilePresenceValidator.class);
    private static final String META_ZIP_PATTERN = ".*\\.meta\\.zip$";
    private static final String BMOTO_PATTERN = "^BMOTOFNZ.*\\.zip$";

    @Override
    public ValidationResult validate(FileValidationContext context) {
        long startTime = System.currentTimeMillis();
        logger.info("Starting file presence validation for directory: {}", context.getFilePath());

        ValidationResult result = ValidationResult.builder()
                .validationType("File Presence Validation")
                .passed(true)
                .details(new HashMap<>())
                .failureDetails(new ArrayList<>())
                .build();

        try {
            String directoryPath = context.getFilePath();
            
            // Find .meta.zip file
            String metaZipFile = findMetaZipFile(directoryPath);
            if (metaZipFile == null) {
                result.setPassed(false);
                result.setErrorMessage("No .meta.zip file found in the directory");
                result.getFailureDetails().add("Missing .meta.zip file");
                logger.error("No .meta.zip file found");
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            logger.info("Found meta ZIP file: {}", metaZipFile);

            // Extract meta ZIP file
            String tempExtractPath = directoryPath + "/meta_extract";
            FileUtil.createDirectory(tempExtractPath);
            FileUtil.extractZipFile(metaZipFile, tempExtractPath);

            // Find CSV file in extracted content
            List<String> extractedFiles = FileUtil.listFiles(tempExtractPath);
            String csvFile = extractedFiles.stream()
                    .filter(f -> f.endsWith(".csv"))
                    .findFirst()
                    .orElse(null);

            if (csvFile == null) {
                result.setPassed(false);
                result.setErrorMessage("No CSV file found in .meta.zip");
                result.getFailureDetails().add("Missing CSV file in meta ZIP");
                logger.error("No CSV file found in .meta.zip");
                result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
                return result;
            }

            // Read CSV file and extract file names
            String csvPath = Paths.get(tempExtractPath, csvFile).toString();
            List<String> csvLines = FileUtil.readFileLines(csvPath);
            Set<String> expectedFiles = new HashSet<>();

            for (String line : csvLines) {
                // Parse format: nosplit=<filename>.zip=<hash>
                String fileName = extractFileNameFromCSVLine(line);
                if (fileName != null && fileName.matches(BMOTO_PATTERN)) {
                    expectedFiles.add(fileName);
                }
            }

            logger.info("Found {} expected files in CSV", expectedFiles.size());

            // Get actual files in directory
            List<String> actualFiles = FileUtil.listFiles(directoryPath).stream()
                    .filter(f -> f.matches(BMOTO_PATTERN))
                    .collect(Collectors.toList());

            logger.info("Found {} actual files in directory", actualFiles.size());

            // Compare files
            Set<String> missingFiles = new HashSet<>(expectedFiles);
            missingFiles.removeAll(actualFiles);

            Set<String> additionalFiles = new HashSet<>(actualFiles);
            additionalFiles.removeAll(expectedFiles);

            if (!missingFiles.isEmpty() || !additionalFiles.isEmpty()) {
                result.setPassed(false);
                
                FileValidationFailure failure = FileValidationFailure.builder()
                        .missingFiles(new ArrayList<>(missingFiles))
                        .additionalFiles(new ArrayList<>(additionalFiles))
                        .csvFileCount(expectedFiles.size())
                        .actualFileCount(actualFiles.size())
                        .build();

                result.getDetails().put("failure", failure);
                
                if (!missingFiles.isEmpty()) {
                    result.getFailureDetails().add(String.format("Missing Files: %s", missingFiles));
                }
                if (!additionalFiles.isEmpty()) {
                    result.getFailureDetails().add(String.format("Additional Files: %s", additionalFiles));
                }
                result.getFailureDetails().add(String.format("Expected Files Count: %d", expectedFiles.size()));
                result.getFailureDetails().add(String.format("Actual Files Count: %d", actualFiles.size()));
                
                logger.warn("File presence validation failed");
            } else {
                logger.info("File presence validation passed successfully");
            }

            // Cleanup
            cleanupDirectory(tempExtractPath);

        } catch (IOException e) {
            logger.error("Error during file presence validation", e);
            result.setPassed(false);
            result.setErrorMessage(e.getMessage());
        }

        result.setExecutionTimeMs(System.currentTimeMillis() - startTime);
        logger.info("File presence validation completed in {} ms", result.getExecutionTimeMs());
        return result;
    }

    /**
     * Find meta ZIP file in directory
     */
    private String findMetaZipFile(String directoryPath) throws IOException {
        List<String> files = FileUtil.listFiles(directoryPath);
        for (String file : files) {
            if (file.matches(META_ZIP_PATTERN)) {
                return Paths.get(directoryPath, file).toString();
            }
        }
        return null;
    }

    /**
     * Extract file name from CSV line
     * Expected format: nosplit=<filename>.zip=<hash>
     */
    private String extractFileNameFromCSVLine(String line) {
        try {
            if (line == null || line.isEmpty()) {
                return null;
            }
            
            // Parse format: nosplit=<filename>.zip=<hash>
            String[] parts = line.split("=");
            if (parts.length >= 2) {
                String fileName = parts[1].trim();
                if (fileName.endsWith(".zip")) {
                    return fileName;
                }
            }
        } catch (Exception e) {
            logger.warn("Error extracting file name from CSV line: {}", line, e);
        }
        return null;
    }

    /**
     * Cleanup temporary directory
     */
    private void cleanupDirectory(String directoryPath) {
        try {
            org.apache.commons.io.FileUtils.deleteDirectory(new java.io.File(directoryPath));
            logger.debug("Cleaned up temporary directory: {}", directoryPath);
        } catch (IOException e) {
            logger.warn("Error cleaning up temporary directory: {}", directoryPath, e);
        }
    }

    @Override
    public String getName() {
        return "File Presence Validator";
    }

    @Override
    public boolean supports(String fileType) {
        return "ZIP".equalsIgnoreCase(fileType) || "DIRECTORY".equalsIgnoreCase(fileType);
    }
}
