package com.sixsprints.json.service;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstrumentDto {

  @JsonProperty("token")
  private Long token;

  @JsonProperty("symbol")
  private String symbol;

  @JsonProperty("name")
  private String name;

  @JsonProperty("expiry")
  private String expiry;

  @JsonProperty("strike")
  private BigDecimal strike;

  @JsonProperty("lotsize")
  private Integer lotSize;

  @JsonProperty("instrumenttype")
  private String instrumentType;

  @JsonProperty("exch_seg")
  private String exchSeg;

  @JsonProperty("tick_size")
  private BigDecimal tickSize;

}
