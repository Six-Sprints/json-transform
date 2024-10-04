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
import com.sixsprints.json.dto.ApiCall;
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

  private static final ObjectMapper MAPPER;

  static {
    MAPPER = new ObjectMapper();
    MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    MAPPER.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    MAPPER.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    MAPPER.setTimeZone(TimeZone.getTimeZone(ZoneId.systemDefault()));
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
    return retrofit(baseUrl, MAPPER);
  }

  public static <T> T create(Class<T> clazz, String baseUrl) {
    return create(clazz, baseUrl, MAPPER);
  }

  @SuppressWarnings("unchecked")
  public static <T> T makeCallAndTransform(ApiCall apiCall, Class<T> clazz)
    throws IOException, ApiException {
    TransformerResponse response = makeCall(apiCall.getCall(), apiCall.getMapping());
    if (isPrimitive(clazz)) {
      return (T) response.getOutput();
    }
    return convertViaMapper(getMapper(apiCall), getMapper(apiCall).getTypeFactory().constructType(clazz), response);
  }

  public static <T> T makeCallAndTransform(ApiCall apiCall, TypeReference<T> type)
    throws IOException, ApiException {
    return makeCallAndTransform(apiCall, getMapper(apiCall).getTypeFactory().constructType(type));
  }

  public static <T> T makeCallAndTransform(ApiCall apiCall, JavaType type)
    throws IOException, ApiException {
    TransformerResponse response = makeCall(apiCall.getCall(), apiCall.getMapping());
    return convertViaMapper(getMapper(apiCall), type, response);
  }

  private static <T> T convertViaMapper(ObjectMapper mapper, JavaType type, TransformerResponse response)
    throws JsonProcessingException, JsonMappingException {

    if (response == null || response.getOutput() == null) {
      return null;
    }
    if (response.getOutput() instanceof String) {
      if (response.getOutput().toString() == null || response.getOutput().toString().equals("")) {
        return null;
      }
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

  private static ObjectMapper getMapper(ApiCall apiCall) {
    return apiCall.getMapper() == null ? MAPPER : apiCall.getMapper();
  }

}
