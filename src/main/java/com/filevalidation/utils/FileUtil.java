package com.filevalidation.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utility class for file operations
 */
public class FileUtil {
    private static final Logger logger = LogManager.getLogger(FileUtil.class);

    /**
     * Extract ZIP file
     */
    public static String extractZipFile(String zipFilePath, String extractPath) throws IOException {
        logger.info("Extracting ZIP file: {}", zipFilePath);
        Path extractPathObj = Paths.get(extractPath);
        if (!Files.exists(extractPathObj)) {
            Files.createDirectories(extractPathObj);
        }

        try (ZipFile zipFile = new ZipFile(zipFilePath)) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            String firstExtractedFile = null;

            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path entryPath = extractPathObj.resolve(entry.getName());

                if (entry.isDirectory()) {
                    Files.createDirectories(entryPath);
                } else {
                    Files.createDirectories(entryPath.getParent());
                    try (InputStream is = zipFile.getInputStream(entry)) {
                        Files.copy(is, entryPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    if (firstExtractedFile == null) {
                        firstExtractedFile = entryPath.toString();
                    }
                }
            }
            logger.info("ZIP file extracted successfully to: {}", extractPath);
            return firstExtractedFile;
        } catch (IOException e) {
            logger.error("Error extracting ZIP file: {}", zipFilePath, e);
            throw e;
        }
    }

    /**
     * Read file content as string
     */
    public static String readFileContent(String filePath) throws IOException {
        try {
            logger.debug("Reading file: {}", filePath);
            return new String(Files.readAllBytes(Paths.get(filePath)));
        } catch (IOException e) {
            logger.error("Error reading file: {}", filePath, e);
            throw e;
        }
    }

    /**
     * Read file lines
     */
    public static List<String> readFileLines(String filePath) throws IOException {
        try {
            logger.debug("Reading file lines: {}", filePath);
            return Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            logger.error("Error reading file lines: {}", filePath, e);
            throw e;
        }
    }

    /**
     * Get all files in directory matching pattern
     */
    public static List<String> getFiles(String directoryPath, String filePattern) throws IOException {
        logger.debug("Getting files from directory: {}", directoryPath);
        List<String> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath), filePattern)) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    files.add(path.toString());
                }
            }
        } catch (IOException e) {
            logger.error("Error getting files from directory: {}", directoryPath, e);
            throw e;
        }
        return files;
    }

    /**
     * List all files in directory
     */
    public static List<String> listFiles(String directoryPath) throws IOException {
        logger.debug("Listing files from directory: {}", directoryPath);
        List<String> files = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(directoryPath))) {
            for (Path path : stream) {
                if (Files.isRegularFile(path)) {
                    files.add(path.getFileName().toString());
                }
            }
        } catch (IOException e) {
            logger.error("Error listing files from directory: {}", directoryPath, e);
            throw e;
        }
        return files;
    }

    /**
     * Check if file exists
     */
    public static boolean fileExists(String filePath) {
        return Files.exists(Paths.get(filePath));
    }

    /**
     * Create directory if not exists
     */
    public static void createDirectory(String directoryPath) throws IOException {
        Path path = Paths.get(directoryPath);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
            logger.debug("Directory created: {}", directoryPath);
        }
    }

    /**
     * Get file size
     */
    public static long getFileSize(String filePath) throws IOException {
        return Files.size(Paths.get(filePath));
    }

    /**
     * Delete file
     */
    public static void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(Paths.get(filePath));
        logger.debug("File deleted: {}", filePath);
    }

    /**
     * Get file extension
     */
    public static String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot + 1).toLowerCase() : "";
    }

    /**
     * Get file name without extension
     */
    public static String getFileNameWithoutExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(0, lastDot) : fileName;
    }

    /**
     * Find meta ZIP file in directory
     */
    public static String findMetaZipFile(String directoryPath) throws IOException {
        logger.info("Searching for .meta.zip file in: {}", directoryPath);
        List<String> files = listFiles(directoryPath);
        for (String file : files) {
            if (file.endsWith(".meta.zip")) {
                logger.info("Found meta ZIP file: {}", file);
                return Paths.get(directoryPath, file).toString();
            }
        }
        logger.warn("No .meta.zip file found in: {}", directoryPath);
        return null;
    }

    /**
     * Find files matching pattern
     */
    public static List<String> findFilesByPattern(String directoryPath, String pattern) throws IOException {
        logger.info("Finding files matching pattern: {} in: {}", pattern, directoryPath);
        List<String> matchingFiles = new ArrayList<>();
        List<String> files = listFiles(directoryPath);
        for (String file : files) {
            if (file.matches(pattern)) {
                matchingFiles.add(file);
            }
        }
        logger.info("Found {} files matching pattern", matchingFiles.size());
        return matchingFiles;
    }

    /**
     * Copy file
     */
    public static void copyFile(String sourcePath, String destinationPath) throws IOException {
        Files.copy(Paths.get(sourcePath), Paths.get(destinationPath), StandardCopyOption.REPLACE_EXISTING);
        logger.debug("File copied from {} to {}", sourcePath, destinationPath);
    }
}
