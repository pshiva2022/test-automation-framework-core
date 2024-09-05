package com.dummycompany.test.framework.core.rerun.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "line",
    "column"
})

public class Location {

  @JsonProperty("line")
  public Long line;
  @JsonProperty("column")
  public Long column;

  public Long getLine() {
    return line;
  }

  public void setLine(Long line) {
    this.line = line;
  }

  public Long getColumn() {
    return column;
  }

  public void setColumn(Long column) {
    this.column = column;
  }
}
