package com.dummycompany.test.framework.core.rerun.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "start_timestamp",
    "before",
    "line",
    "name",
    "description",
    "id",
    "after",
    "type",
    "keyword",
    "steps",
    "tags"
})

public class Element {

  @JsonProperty("start_timestamp")
  public String startTimestamp;

  @JsonProperty("before")
  public List<Before> before = null;

  @JsonProperty("line")
  public Long line;

  @JsonProperty("name")
  public String name;

  @JsonProperty("description")
  public String description;

  @JsonProperty("id")
  public String id;

  @JsonProperty("after")
  public List<After> after = null;

  @JsonProperty("type")
  public String type;

  @JsonProperty("keyword")
  public String keyword;

  @JsonProperty("steps")
  public List<Step> steps = null;

  @JsonProperty("tags")
  public List<ElementTag> elementTags = null;

  public String getStartTimestamp() {
    return startTimestamp;
  }

  public void setStartTimestamp(String startTimestamp) {
    this.startTimestamp = startTimestamp;
  }

  public List<Before> getBefore() {
    return before;
  }

  public void setBefore(List<Before> before) {
    this.before = before;
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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public List<After> getAfter() {
    return after;
  }

  public void setAfter(List<After> after) {
    this.after = after;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public List<Step> getSteps() {
    return steps;
  }

  public void setSteps(List<Step> steps) {
    this.steps = steps;
  }

  public List<ElementTag> getElementTags() {
    return elementTags;
  }

  public void setElementTags(List<ElementTag> elementTags) {
    this.elementTags = elementTags;
  }
}
