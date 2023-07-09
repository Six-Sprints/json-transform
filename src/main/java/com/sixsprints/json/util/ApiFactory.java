package com.sixsprints.json.util;

import java.io.IOException;
import java.time.ZoneId;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Primitives;
import com.sixsprints.json.dto.Mapping;
import com.sixsprints.json.dto.TransformerResponse;
import com.sixsprints.json.exception.ApiException;
import com.sixsprints.json.service.MappingService;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiFactory {

  private static final ObjectMapper mapper;

  static {
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
  }

  public static <T> T create(Class<T> clazz, String baseUrl, ObjectMapper mapper) {
    Retrofit retrofit = retrofit(baseUrl, mapper).build();
    return retrofit.create(clazz);
  }

  public static Builder retrofit(String baseUrl, ObjectMapper mapper) {
    return new Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(JacksonConverterFactory.create(mapper));
  }

  public static Builder retrofit(String baseUrl) {
    return retrofit(baseUrl, mapper);
  }

  public static <T> T create(Class<T> clazz, String baseUrl) {
    return create(clazz, baseUrl, mapper);
  }

  @SuppressWarnings("unchecked")
  public static <T> T makeCallAndTransform(Call<String> call, Class<T> clazz, Mapping mapping)
    throws IOException, ApiException {
    TransformerResponse response = makeCall(call, mapping);
    if (isPrimitive(clazz)) {
      return (T) response.getOutput();
    }
    return convertViaMapper(mapper.getTypeFactory().constructType(clazz), response);
  }

  public static <T> T makeCallAndTransform(Call<String> call, TypeReference<T> type, Mapping mapping)
    throws IOException, ApiException {
    return makeCallAndTransform(call, mapper.getTypeFactory().constructType(type), mapping);
  }

  public static <T> T makeCallAndTransform(Call<String> call, JavaType type, Mapping mapping)
    throws IOException, ApiException {
    TransformerResponse response = makeCall(call, mapping);
    return convertViaMapper(type, response);
  }

  private static <T> T convertViaMapper(JavaType type, TransformerResponse response)
    throws JsonProcessingException, JsonMappingException {
    if (response.getOutput() instanceof String) {
      return mapper.readValue(response.getOutput().toString(), type);
    }
    return mapper.convertValue(response.getOutput(), type);
  }

  private static <T> boolean isPrimitive(Class<T> clazz) {
    return Primitives.isWrapperType(clazz);
  }

  private static <T> TransformerResponse makeCall(Call<String> call, Mapping mapping)
    throws IOException, JsonProcessingException, JsonMappingException, ApiException {
    Response<String> response = call.execute();
    if (response.isSuccessful()) {
      return MappingService.convert(mapping, response.body());
    }
    throw ApiException.builder().response(response).error("Response was unsuccessfull").build();
  }

}
