package com.filevalidation.exceptions;

/**
 * Exception thrown during file operations
 */
public class FileOperationException extends Exception {
    private static final long serialVersionUID = 1L;

    public FileOperationException(String message) {
        super(message);
    }

    public FileOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
