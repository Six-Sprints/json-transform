package com.sixsprints.json.util;

import java.util.HashMap;
import java.util.Map;

import com.sixsprints.json.service.SpecTransformer;

public class SpecFactory {

  private static final Map<String, SpecTransformer> MAP = new HashMap<>();

  public static void register(Map<String, SpecTransformer> map) {
    MAP.putAll(map);
  }

  public static SpecTransformer getInstance(String key) {
    SpecTransformer specTransformer = MAP.get(key);
    if (specTransformer == null) {
      throw new IllegalArgumentException("No Spec Transformer found with key = " + key);
    }
    return specTransformer;
  }

}
