package com.filevalidation.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Model for file configuration
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileConfig {
    @JsonProperty("fileName")
    private String fileName;

    @JsonProperty("delimiter")
    private String delimiter;

    @JsonProperty("hasHeader")
    private boolean hasHeader;

    @JsonProperty("expectedHeaders")
    private List<String> expectedHeaders;

    @JsonProperty("notNullColumns")
    private List<String> notNullColumns;

    @JsonProperty("dateFormat")
    private String dateFormat;

    @JsonProperty("fileType")
    private String fileType;

    @JsonProperty("skipValidations")
    private List<String> skipValidations;
}
