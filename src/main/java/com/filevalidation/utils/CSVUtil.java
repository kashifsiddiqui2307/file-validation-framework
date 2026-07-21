package com.filevalidation.utils;

import com.filevalidation.models.FileValidationContext;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for CSV operations
 */
public class CSVUtil {
    private static final Logger logger = LogManager.getLogger(CSVUtil.class);

    /**
     * Parse CSV content
     */
    public static List<String[]> parseCSV(String content, String delimiter) {
        List<String[]> records = new ArrayList<>();
        try {
            CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(delimiter.charAt(0));
            StringReader reader = new StringReader(content);
            CSVParser parser = csvFormat.parse(reader);

            for (CSVRecord record : parser) {
                String[] row = new String[(int) record.size()];
                for (int i = 0; i < record.size(); i++) {
                    row[i] = record.get(i);
                }
                records.add(row);
            }
            parser.close();
        } catch (IOException e) {
            logger.error("Error parsing CSV content", e);
        }
        return records;
    }

    /**
     * Parse CSV file
     */
    public static List<String[]> parseCSVFile(String filePath, String delimiter) throws IOException {
        List<String[]> records = new ArrayList<>();
        try {
            CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(delimiter.charAt(0));
            Reader in = Files.newBufferedReader(Paths.get(filePath), StandardCharsets.UTF_8);
            CSVParser parser = csvFormat.parse(in);

            for (CSVRecord record : parser) {
                String[] row = new String[(int) record.size()];
                for (int i = 0; i < record.size(); i++) {
                    row[i] = record.get(i);
                }
                records.add(row);
            }
            parser.close();
            logger.info("Successfully parsed CSV file: {}", filePath);
        } catch (IOException e) {
            logger.error("Error parsing CSV file: {}", filePath, e);
            throw e;
        }
        return records;
    }

    /**
     * Get CSV headers
     */
    public static List<String> getHeaders(List<String[]> records) {
        if (records == null || records.isEmpty()) {
            return Collections.emptyList();
        }
        return Arrays.asList(records.get(0));
    }

    /**
     * Get CSV data rows (excluding header)
     */
    public static List<String[]> getDataRows(List<String[]> records) {
        if (records == null || records.size() <= 1) {
            return Collections.emptyList();
        }
        return records.subList(1, records.size());
    }

    /**
     * Get trailer record if exists
     */
    public static String[] getTrailerRecord(List<String[]> records) {
        if (records == null || records.isEmpty()) {
            return null;
        }
        String[] lastRecord = records.get(records.size() - 1);
        if (lastRecord.length > 0 && lastRecord[0].startsWith("TRL")) {
            return lastRecord;
        }
        return null;
    }

    /**
     * Count data records (excluding header and trailer)
     */
    public static int countDataRecords(List<String[]> records) {
        if (records == null || records.size() <= 1) {
            return 0;
        }

        int count = records.size() - 1; // Exclude header
        String[] lastRecord = records.get(records.size() - 1);
        if (lastRecord.length > 0 && lastRecord[0].startsWith("TRL")) {
            count--; // Exclude trailer
        }
        return count;
    }

    /**
     * Get field value at specific column index
     */
    public static String getFieldValue(String[] record, int columnIndex) {
        if (record == null || columnIndex < 0 || columnIndex >= record.length) {
            return null;
        }
        return record[columnIndex];
    }

    /**
     * Get column index by header name
     */
    public static int getColumnIndex(List<String> headers, String columnName) {
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).equals(columnName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Find multiple column indices by header names
     */
    public static Map<String, Integer> getColumnIndices(List<String> headers, List<String> columnNames) {
        Map<String, Integer> indices = new HashMap<>();
        for (String columnName : columnNames) {
            int index = getColumnIndex(headers, columnName);
            if (index >= 0) {
                indices.put(columnName, index);
            }
        }
        return indices;
    }

    /**
     * Write CSV file
     */
    public static void writeCSV(String filePath, List<String[]> records, String delimiter) throws IOException {
        try (Writer writer = Files.newBufferedWriter(Paths.get(filePath), StandardCharsets.UTF_8)) {
            CSVFormat csvFormat = CSVFormat.DEFAULT.withDelimiter(delimiter.charAt(0));
            org.apache.commons.csv.CSVPrinter printer = new org.apache.commons.csv.CSVPrinter(writer, csvFormat);
            for (String[] record : records) {
                printer.printRecord((Object[]) record);
            }
            printer.flush();
            logger.info("Successfully wrote CSV file: {}", filePath);
        } catch (IOException e) {
            logger.error("Error writing CSV file: {}", filePath, e);
            throw e;
        }
    }
}
