package com.filevalidation.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model to hold validation context
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileValidationContext {
    private String filePath;
    private String fileName;
    private FileConfig fileConfig;
    private String fileType;
    private List<String> fileLines;
    private List<String[]> parsedData;
}
