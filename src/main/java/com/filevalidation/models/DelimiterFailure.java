package com.filevalidation.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model to hold delimiter validation failure details
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DelimiterFailure {
    private String columnName;
    private List<Integer> rowNumbers;
}
