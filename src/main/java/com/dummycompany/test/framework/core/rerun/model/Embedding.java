package com.dummycompany.test.framework.core.rerun.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "data",
    "mime_type"
})

public class Embedding {

  @JsonProperty("data")
  public String data;

  @JsonProperty("mime_type")
  public String mimeType;

  @JsonProperty("name")
  public String name;

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public String getMimeType() {
    return mimeType;
  }

  public void setMimeType(String mimeType) {
    this.mimeType = mimeType;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
