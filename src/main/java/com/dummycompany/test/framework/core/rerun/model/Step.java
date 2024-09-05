package com.dummycompany.test.framework.core.rerun.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "output",
    "embeddings",
    "result",
    "line",
    "name",
    "match",
    "rows",
    "keyword",
    "before",
    "after"
})
public class Step {

  @JsonProperty("output")
  public List<String> output;

  @JsonProperty("embeddings")
  public List<Embedding> embeddings;

  @JsonProperty("result")
  public Result result;

  @JsonProperty("line")
  public Long line;

  @JsonProperty("name")
  public String name;

  @JsonProperty("match")
  public Match match;

  @JsonProperty("rows")
  public List<Rows> rows;

  @JsonProperty("keyword")
  public String keyword;

  @JsonProperty("before")
  public List<Before> befores;

  @JsonProperty("after")
  public List<After> afters;

  public List<String> getOutput() {
    return output;
  }

  public void setOutput(List<String> output) {
    this.output = output;
  }

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

  public Long getLine() {
    return line;
  }

  public void setLine(Long line) {
    this.line = line;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Match getMatch() {
    return match;
  }

  public void setMatch(Match match) {
    this.match = match;
  }

  public List<Rows> getRows() {
    return rows;
  }

  public void setRows(List<Rows> rows) {
    this.rows = rows;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public List<Before> getBefores() {
    return befores;
  }

  public void setBefores(List<Before> befores) {
    this.befores = befores;
  }

  public List<After> getAfters() {
    return afters;
  }

  public void setAfters(List<After> afters) {
    this.afters = afters;
  }
}
