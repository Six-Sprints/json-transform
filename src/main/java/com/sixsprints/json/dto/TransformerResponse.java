package com.sixsprints.json.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransformerResponse {

  private Map<String, Object> output;

  private List<TransformerMetaInfo> transformerMetaInfo;

}
