package com.sixsprints.json.service;

import java.io.InputStream;
import java.util.List;

import com.sixsprints.json.dto.Mapping;
import com.sixsprints.json.dto.TransformerData;
import com.sixsprints.json.dto.TransformerResponse;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class MappingServiceTest extends TestCase {

  public MappingServiceTest(String testName) {
    super(testName);
  }

  public static Test suite() {
    return new TestSuite(MappingServiceTest.class);
  }

  public void testShouldConvert() {

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
