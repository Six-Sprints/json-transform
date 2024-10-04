package com.sixsprints.json.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestDto<T> {

  private Boolean status;

  private String message;

  private String errorcode;

  private T data;

}