package com.filevalidation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Utility class for date and format operations
 */
public class DateUtil {
    private static final Logger logger = LogManager.getLogger(DateUtil.class);

    /**
     * Validate date format YYYYMMDDHHMMSS
     */
    public static boolean validateDateFormat(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return false;
        }

        // Check if it matches YYYYMMDDHHMMSS pattern
        String pattern = "^\\d{14}$";
        if (!Pattern.matches(pattern, dateString)) {
            logger.warn("Date string does not match YYYYMMDDHHMMSS pattern: {}", dateString);
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            sdf.setLenient(false);
            sdf.parse(dateString);
            logger.debug("Date format validation passed for: {}", dateString);
            return true;
        } catch (ParseException e) {
            logger.warn("Invalid date format for: {}", dateString, e);
            return false;
        }
    }

    /**
     * Extract date from filename (format: YYYYMMDDHHMMSS at the end before extension)
     */
    public static String extractDateFromFileName(String fileName) {
        // Remove extension
        String nameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));

        // Extract last 14 characters (YYYYMMDDHHMMSS)
        if (nameWithoutExt.length() >= 14) {
            return nameWithoutExt.substring(nameWithoutExt.length() - 14);
        }
        return null;
    }

    /**
     * Validate date format with custom pattern
     */
    public static boolean validateDateFormatWithPattern(String dateString, String pattern) {
        if (dateString == null || dateString.isEmpty()) {
            return false;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);
            sdf.setLenient(false);
            sdf.parse(dateString);
            logger.debug("Date format validation passed for: {} with pattern: {}", dateString, pattern);
            return true;
        } catch (ParseException e) {
            logger.warn("Invalid date format for: {} with pattern: {}", dateString, pattern, e);
            return false;
        }
    }

    /**
     * Parse date string
     */
    public static Date parseDate(String dateString, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        return sdf.parse(dateString);
    }

    /**
     * Format date to string
     */
    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(date);
    }
}
