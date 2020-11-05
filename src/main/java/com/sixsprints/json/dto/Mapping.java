package com.sixsprints.json.dto;

import java.io.InputStream;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Mapping {

  private String rootElement;

  private String specJsonString;

  private InputStream specJsonStream;

  private List<TransformerData> transformerData;

}
