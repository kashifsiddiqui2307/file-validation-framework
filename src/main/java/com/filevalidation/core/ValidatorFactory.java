package com.filevalidation.core;

import com.filevalidation.validators.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Factory for creating and managing validators
 */
public class ValidatorFactory {
    private static final Logger logger = LogManager.getLogger(ValidatorFactory.class);
    private static final Map<String, Class<? extends FileValidator>> validatorRegistry = new HashMap<>();

    static {
        // Register all available validators
        registerValidator("delimiter", DelimiterValidator.class);
        registerValidator("trailer", TrailerValidator.class);
        registerValidator("file-presence", FilePresenceValidator.class);
        registerValidator("date-format", DateFormatValidator.class);
        registerValidator("headers", HeaderValidator.class);
        registerValidator("not-null", NotNullValidator.class);
    }

    /**
     * Register a new validator
     */
    public static void registerValidator(String name, Class<? extends FileValidator> validatorClass) {
        validatorRegistry.put(name, validatorClass);
        logger.info("Registered validator: {} -> {}", name, validatorClass.getSimpleName());
    }

    /**
     * Create validator by name
     */
    public static FileValidator createValidator(String validatorName) {
        Class<? extends FileValidator> validatorClass = validatorRegistry.get(validatorName);
        if (validatorClass == null) {
            logger.warn("Validator not found: {}", validatorName);
            return null;
        }

        try {
            FileValidator validator = validatorClass.getDeclaredConstructor().newInstance();
            logger.debug("Created validator instance: {}", validatorName);
            return validator;
        } catch (Exception e) {
            logger.error("Error creating validator: {}", validatorName, e);
            return null;
        }
    }

    /**
     * Get all registered validators
     */
    public static List<FileValidator> getAllValidators() {
        List<FileValidator> validators = new ArrayList<>();
        for (String name : validatorRegistry.keySet()) {
            FileValidator validator = createValidator(name);
            if (validator != null) {
                validators.add(validator);
            }
        }
        logger.info("Created {} validators", validators.size());
        return validators;
    }

    /**
     * Get validator names
     */
    public static List<String> getValidatorNames() {
        return new ArrayList<>(validatorRegistry.keySet());
    }
}
