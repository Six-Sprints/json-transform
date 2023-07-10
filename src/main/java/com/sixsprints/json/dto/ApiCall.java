package com.sixsprints.json.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import retrofit2.Call;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiCall {

  private Call<String> call;

  private Mapping mapping;

  private ObjectMapper mapper;

}
