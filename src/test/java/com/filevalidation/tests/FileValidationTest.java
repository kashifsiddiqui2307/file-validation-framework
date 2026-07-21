package com.filevalidation.tests;

import com.filevalidation.core.ValidationEngine;
import com.filevalidation.models.FileConfig;
import com.filevalidation.models.ValidationResult;
import com.filevalidation.utils.FileUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * TestNG test class for File Validation Framework
 */
public class FileValidationTest {
    private static final Logger logger = LogManager.getLogger(FileValidationTest.class);
    private ValidationEngine validationEngine;
    private static final String TEST_DATA_DIR = "src/test/resources/data";
    private static final String TEST_CONFIG_FILE = "src/test/resources/config/validation-config.json";

    @BeforeClass
    public void setUp() {
        logger.info("=== Setting up Test Suite ===");
        validationEngine = new ValidationEngine();
        logger.info("ValidationEngine initialized successfully");
    }

    @BeforeMethod
    public void beforeMethod() {
        logger.info("\n--- Starting Test Method ---");
    }

    @AfterMethod
    public void afterMethod() {
        logger.info("--- Test Method Completed ---\n");
    }

    @Test(priority = 1, description = "Test configuration loading")
    public void testConfigurationLoading() {
        logger.info("TEST: Configuration Loading");
        try {
            validationEngine.loadConfiguration(TEST_CONFIG_FILE);
            Assert.assertNotNull(validationEngine, "ValidationEngine should not be null after configuration load");
            logger.info("✓ Configuration loaded successfully");
        } catch (IOException e) {
            logger.error("Failed to load configuration", e);
            Assert.fail("Configuration loading failed: " + e.getMessage());
        }
    }

    @Test(priority = 2, description = "Test file validation context creation")
    public void testFileValidationContextCreation() {
        logger.info("TEST: File Validation Context Creation");
        try {
            // Create sample CSV data
            String testData = "ID,Name,Date\n1,John,2023-01-01\n2,Jane,2023-01-02";
            String testFile = TEST_DATA_DIR + "/test_sample.csv";
            
            FileUtil.createDirectory(TEST_DATA_DIR);
            Files.write(Paths.get(testFile), testData.getBytes());

            FileConfig fileConfig = FileConfig.builder()
                    .fileName("test_sample.csv")
                    .delimiter(",")
                    .hasHeader(true)
                    .fileType("CSV")
                    .build();

            ValidationResult result = validationEngine.validateFile(testFile, fileConfig);
            Assert.assertNotNull(result, "ValidationResult should not be null");
            logger.info("✓ File validation context created successfully");
        } catch (Exception e) {
            logger.error("Failed to create validation context", e);
            Assert.fail("File validation context creation failed: " + e.getMessage());
        }
    }

    @Test(priority = 3, description = "Test delimiter validation")
    public void testDelimiterValidation() {
        logger.info("TEST: Delimiter Validation");
        try {
            // Create sample CSV data with proper delimiters
            String testData = "ID|Name|Date\n1|John|2023-01-01\n2|Jane|2023-01-02";
            String testFile = TEST_DATA_DIR + "/test_delimiter.csv";
            
            FileUtil.createDirectory(TEST_DATA_DIR);
            Files.write(Paths.get(testFile), testData.getBytes());

            FileConfig fileConfig = FileConfig.builder()
                    .fileName("test_delimiter.csv")
                    .delimiter("|")
                    .hasHeader(true)
                    .fileType("CSV")
                    .build();

            ValidationResult result = validationEngine.validateFile(testFile, fileConfig);
            Assert.assertNotNull(result, "ValidationResult should not be null");
            logger.info("✓ Delimiter validation test passed");
        } catch (Exception e) {
            logger.error("Delimiter validation test failed", e);
            Assert.fail("Delimiter validation failed: " + e.getMessage());
        }
    }

    @Test(priority = 4, description = "Test header validation")
    public void testHeaderValidation() {
        logger.info("TEST: Header Validation");
        try {
            // Create sample CSV data
            String testData = "ID,Name,Date\n1,John,2023-01-01\n2,Jane,2023-01-02";
            String testFile = TEST_DATA_DIR + "/test_headers.csv";
            
            FileUtil.createDirectory(TEST_DATA_DIR);
            Files.write(Paths.get(testFile), testData.getBytes());

            FileConfig fileConfig = FileConfig.builder()
                    .fileName("test_headers.csv")
                    .delimiter(",")
                    .hasHeader(true)
                    .expectedHeaders(List.of("ID", "Name", "Date"))
                    .fileType("CSV")
                    .build();

            ValidationResult result = validationEngine.validateFile(testFile, fileConfig);
            Assert.assertNotNull(result, "ValidationResult should not be null");
            logger.info("✓ Header validation test passed");
        } catch (Exception e) {
            logger.error("Header validation test failed", e);
            Assert.fail("Header validation failed: " + e.getMessage());
        }
    }

    @Test(priority = 5, description = "Test not-null validation")
    public void testNotNullValidation() {
        logger.info("TEST: Not-Null Validation");
        try {
            // Create sample CSV data with all values
            String testData = "ID,Name,Date\n1,John,2023-01-01\n2,Jane,2023-01-02";
            String testFile = TEST_DATA_DIR + "/test_not_null.csv";
            
            FileUtil.createDirectory(TEST_DATA_DIR);
            Files.write(Paths.get(testFile), testData.getBytes());

            FileConfig fileConfig = FileConfig.builder()
                    .fileName("test_not_null.csv")
                    .delimiter(",")
                    .hasHeader(true)
                    .notNullColumns(List.of("ID", "Name"))
                    .fileType("CSV")
                    .build();

            ValidationResult result = validationEngine.validateFile(testFile, fileConfig);
            Assert.assertNotNull(result, "ValidationResult should not be null");
            logger.info("✓ Not-null validation test passed");
        } catch (Exception e) {
            logger.error("Not-null validation test failed", e);
            Assert.fail("Not-null validation failed: " + e.getMessage());
        }
    }

    @Test(priority = 6, description = "Test validation results summary")
    public void testValidationSummary() {
        logger.info("TEST: Validation Summary");
        try {
            // Get validation summary
            String summary = validationEngine.getResultsSummary();
            Assert.assertNotNull(summary, "Validation summary should not be null");
            Assert.assertTrue(summary.contains("VALIDATION SUMMARY"), "Summary should contain summary header");
            logger.info("Validation Summary: " + summary);
            logger.info("✓ Validation summary test passed");
        } catch (Exception e) {
            logger.error("Validation summary test failed", e);
            Assert.fail("Validation summary test failed: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        logger.info("=== Tearing Down Test Suite ===");
        logger.info("Test Suite Completed");
    }
}
