package com.sixsprints.json.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransformerMetaInfo {

  private String idKey;

  private String idValue;

  private String transformerKey;

  private List<TransformerChange> changes;

}
