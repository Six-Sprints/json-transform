package com.sixsprints.json.service;

import java.util.List;
import java.util.Map;

import com.sixsprints.json.dto.TransformerChange;

public interface SpecTransformer {

  String CONTEXT_ID_KEY = "contextIdKey";

  List<TransformerChange> transform(Map<String, Object> originalJson, Map<String, Object> transformedJson,
    Map<String, Object> context);

}
