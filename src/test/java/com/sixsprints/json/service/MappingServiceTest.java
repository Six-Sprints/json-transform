package com.sixsprints.json.service;

import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.sixsprints.json.dto.Mapping;
import com.sixsprints.json.dto.TransformerData;
import com.sixsprints.json.dto.TransformerResponse;

public class MappingServiceTest {

  @Test
  public void shouldConvert() {

    String input = "{\n" +
      "  \"rating\": {\n" +
      "    \"primary\": {\n" +
      "      \"value\": 3\n" +
      "    },\n" +
      "    \"quality\": {\n" +
      "      \"value\": 3\n" +
      "    }\n" +
      "  }\n" +
      "}";

    String fileName = "/simple-test-spec.json";
    List<TransformerData> data = null;
    Mapping mapping = mapping(fileName, data);

    TransformerResponse response = MappingService.convert(mapping, input);

    System.out.println(response);

  }

  private Mapping mapping(String fileName, List<TransformerData> data) {

    InputStream stream = this.getClass().getResourceAsStream(fileName);

    return Mapping.builder().specJsonStream(stream).transformerData(data).build();
  }

}
