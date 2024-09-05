package com.dummycompany.test.framework.core.newreport;

import java.util.List;

public class DetailedStatusTableModel {

  private int srNo;
  private String featureName;
  private String featureDescription;
  private String featureFilePath;
  private String featureTags;
  private List<ScenarioTableModel> scenarioTableModels;

  public int getSrNo() {
    return srNo;
  }

  public void setSrNo(int srNo) {
    this.srNo = srNo;
  }

  public String getFeatureName() {
    return featureName;
  }

  public void setFeatureName(String featureName) {
    this.featureName = featureName;
  }

  public String getFeatureDescription() {
    return featureDescription;
  }

  public void setFeatureDescription(String featureDescription) {
    this.featureDescription = featureDescription;
  }

  public String getFeatureFilePath() {
    return featureFilePath;
  }

  public void setFeatureFilePath(String featureFilePath) {
    this.featureFilePath = featureFilePath;
  }

  public String getFeatureTags() {
    return featureTags;
  }

  public void setFeatureTags(String featureTags) {
    this.featureTags = featureTags;
  }

  public List<ScenarioTableModel> getScenarioTableModels() {
    return scenarioTableModels;
  }

  public void setScenarioTableModels(List<ScenarioTableModel> scenarioTableModels) {
    this.scenarioTableModels = scenarioTableModels;
  }

  @Override
  public String toString() {
    return "DetailedStatusTableModel{" +
        "srNo=" + srNo +
        ", featureName='" + featureName + '\'' +
        ", featureDescription='" + featureDescription + '\'' +
        ", featureFilePath='" + featureFilePath + '\'' +
        ", featureTags='" + featureTags + '\'' +
        ", scenarioTableModels=" + scenarioTableModels +
        '}';
  }
}
