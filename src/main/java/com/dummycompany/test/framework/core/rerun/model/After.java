package com.dummycompany.test.framework.core.rerun.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"embeddings", "result", "match", "output"})

public class After {

  @JsonProperty("embeddings")
  public List<Embedding> embeddings = null;

  @JsonProperty("result")
  public Result result;

  @JsonProperty("match")
  public Match match;

  @JsonProperty("output")
  public List<String> output = null;

  public List<Embedding> getEmbeddings() {
    return embeddings;
  }

  public void setEmbeddings(List<Embedding> embeddings) {
    this.embeddings = embeddings;
  }

  public Result getResult() {
    return result;
  }

  public void setResult(Result result) {
    this.result = result;
  }

  public Match getMatch() {
    return match;
  }

  public void setMatch(Match match) {
    this.match = match;
  }

  public List<String> getOutput() {
    return output;
  }

  public void setOutput(List<String> output) {
    this.output = output;
  }
}
