package com.sixsprints.json.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import retrofit2.Response;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ApiException extends Exception {

  private static final long serialVersionUID = -8305613602003002127L;

  private String error;

  private Response<String> response;

  @Override
  public String getMessage() {
    return error;
  }

}
