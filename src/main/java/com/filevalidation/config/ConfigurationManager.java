package com.filevalidation.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.filevalidation.models.ValidationConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

/**
 * Configuration manager for validation framework
 */
public class ConfigurationManager {
    private static final Logger logger = LogManager.getLogger(ConfigurationManager.class);
    private static final ObjectMapper mapper = new ObjectMapper();
    private ValidationConfig validationConfig;

    /**
     * Load configuration from JSON file
     */
    public void loadConfiguration(String configFilePath) throws IOException {
        logger.info("Loading configuration from: {}", configFilePath);
        try {
            validationConfig = mapper.readValue(new File(configFilePath), ValidationConfig.class);
            logger.info("Configuration loaded successfully with {} file configurations", validationConfig.getFiles().size());
        } catch (IOException e) {
            logger.error("Error loading configuration from: {}", configFilePath, e);
            throw e;
        }
    }

    /**
     * Get validation configuration
     */
    public ValidationConfig getValidationConfig() {
        return validationConfig;
    }

    /**
     * Get configuration as JSON string (for logging)
     */
    public String getConfigurationAsString() throws IOException {
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(validationConfig);
    }
}
