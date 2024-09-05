package com.dummycompany.test.framework.core.rerun.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "val",
    "offset"
})

public class Argument {

  @JsonProperty("val")
  public String val;
  @JsonProperty("offset")
  public Long offset;

  public String getVal() {
    return val;
  }

  public void setVal(String val) {
    this.val = val;
  }

  public Long getOffset() {
    return offset;
  }

  public void setOffset(Long offset) {
    this.offset = offset;
  }
}
