package com.filevalidation.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Validation configuration wrapper
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationConfig {
    private List<FileConfig> files;
}
