package com.dummycompany.test.framework.core.rerun.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "line",
    "elements",
    "name",
    "description",
    "id",
    "keyword",
    "uri",
    "tags"
})

public class Feature {

  @JsonProperty("line")
  public Long line;

  @JsonProperty("elements")
  public List<Element> elements;

  @JsonProperty("name")
  public String name;

  @JsonProperty("description")
  public String description;

  @JsonProperty("id")
  public String id;

  @JsonProperty("keyword")
  public String keyword;

  @JsonProperty("uri")
  public String uri;

  @JsonProperty("tags")
  public List<FeatureTags> tags;

  public Long getLine() {
    return line;
  }

  public void setLine(Long line) {
    this.line = line;
  }

  public List<Element> getElements() {
    return elements;
  }

  public void setElements(List<Element> elements) {
    this.elements = elements;
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

  public String getKeyword() {
    return keyword;
  }

  public void setKeyword(String keyword) {
    this.keyword = keyword;
  }

  public String getUri() {
    return uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }

  public List<FeatureTags> getTags() {
    return tags;
  }

  public void setTags(List<FeatureTags> tags) {
    this.tags = tags;
  }
}
