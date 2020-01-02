package com.sixsprints.json.util;

import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiFactory {

  public static ObjectMapper defaultMapper() {
    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    mapper.setTimeZone(DateTimeZone.forID("+05:30").toTimeZone());
    return mapper;
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
    return create(clazz, baseUrl, defaultMapper());
  }

}
