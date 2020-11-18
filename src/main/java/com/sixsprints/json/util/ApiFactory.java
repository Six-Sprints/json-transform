package com.sixsprints.json.util;

import java.io.IOException;

import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.primitives.Primitives;
import com.sixsprints.json.dto.Mapping;
import com.sixsprints.json.dto.TransformerResponse;
import com.sixsprints.json.exception.ApiException;
import com.sixsprints.json.service.MappingService;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiFactory {

  private static final ObjectMapper mapper;

  static {
    mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setTimeZone(DateTimeZone.forID("+05:30").toTimeZone());
  }

  public static <T> T create(Class<T> clazz, String baseUrl, ObjectMapper mapper) {
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(baseUrl)
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(JacksonConverterFactory.create(mapper))
      .build();
    return retrofit.create(clazz);
  }

  public static <T> T create(Class<T> clazz, String baseUrl) {
    return create(clazz, baseUrl, mapper);
  }

  @SuppressWarnings("unchecked")
  public static <T> T makeCallAndTransform(Call<String> call, Class<T> clazz, Mapping mapping)
    throws IOException, ApiException {
    Response<String> response = call.execute();
    if (response.isSuccessful()) {
      TransformerResponse convert = MappingService.convert(mapping, response.body());
      if (isPrimitive(clazz)) {
        return (T) convert.getOutput();
      }
      return mapper.convertValue(convert.getOutput(), clazz);
    }
    throw ApiException.builder().response(response).error("Response was unsuccessfull").build();
  }

  private static <T> boolean isPrimitive(Class<T> clazz) {
    return Primitives.isWrapperType(clazz);
  }

}
