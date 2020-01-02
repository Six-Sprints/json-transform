package com.sixsprints.json.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransformerChange {

  private String originalValue;

  private String transformedValue;

  // Absolute path in the JSON Tree. For e.g. addresses.0.city => 1st city in
  // addresses array
  private String path;

  // Path containing wild cards. For e.g. addresses.*.city => any city in
  // addresses array
  private String loosePath;

  private String idKey;

  private String idValue;

  private String apiName;

}
