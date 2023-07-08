package com.sixsprints.json.service;

import java.io.InputStream;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sixsprints.json.dto.Mapping;
import com.sixsprints.json.dto.TransformerData;
import com.sixsprints.json.dto.TransformerResponse;

public class MappingServiceTest {

  private static final ObjectMapper mapper;

  static {
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
  }

  @Test
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

  @Test
  public void testShouldConvertFromExtractValue() {

    String input = "{\"data\":true,\"success\":false,\"errorMessage\": \"this is an error\",\"errorCode\":10,\"meta\":null}";

    TransformerResponse response = MappingService.convert(Mapping.builder().extractValue("data").build(), input);
    Boolean result = (Boolean) response.getOutput();
    System.out.println(result);
  }

  @Test
  public void mapperShouldConvert() throws JsonMappingException, JsonProcessingException {

    String input = "[{\"token\":\"19441\",\"symbol\":\"AHLEAST-BL\",\"name\":\"AHLEAST\",\"expiry\":\"\",\"strike\":\"-1.000000\",\"lotsize\":\"1\",\"instrumenttype\":\"\",\"exch_seg\":\"NSE\",\"tick_size\":\"5.000000\"},{\"token\":\"8521\",\"symbol\":\"SPRL-SM\",\"name\":\"SPRL\",\"expiry\":\"\",\"strike\":\"-1.000000\",\"lotsize\":\"1600\",\"instrumenttype\":\"\",\"exch_seg\":\"NSE\",\"tick_size\":\"5.000000\"},{\"token\":\"10950\",\"symbol\":\"TMB-BL\",\"name\":\"TMB\",\"expiry\":\"\",\"strike\":\"-1.000000\",\"lotsize\":\"1\",\"instrumenttype\":\"\",\"exch_seg\":\"NSE\",\"tick_size\":\"5.000000\"}]";
    
    System.out.println(input);
    TypeReference<List<InstrumentDto>> type = new TypeReference<List<InstrumentDto>>() {
    };
    List<InstrumentDto> instruments = mapper.readValue(input, type);
    System.out.println(instruments);
  }

  private Mapping mapping(String fileName, List<TransformerData> data) {
    InputStream stream = this.getClass().getResourceAsStream(fileName);
    return Mapping.builder().specJsonStream(stream).transformerData(data).build();
  }

}
