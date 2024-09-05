package com.dummycompany.test.framework.core.newreport;

public class SummaryTableModel {

  private int srNo;
  private String featureName;
  private String featureStatus;
  private int totalScenarios;
  private int totalPassed;
  private int totalFailed;

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

  public String getFeatureStatus() {
    return featureStatus;
  }

  public void setFeatureStatus(String featureStatus) {
    this.featureStatus = featureStatus;
  }

  public int getTotalScenarios() {
    return totalScenarios;
  }

  public void setTotalScenarios(int totalScenarios) {
    this.totalScenarios = totalScenarios;
  }

  public int getTotalPassed() {
    return totalPassed;
  }

  public void setTotalPassed(int totalPassed) {
    this.totalPassed = totalPassed;
  }

  public int getTotalFailed() {
    return totalFailed;
  }

  public void setTotalFailed(int totalFailed) {
    this.totalFailed = totalFailed;
  }

  @Override
  public String toString() {
    return "SummaryTableModel{" +
        "srNo=" + srNo +
        ", featureName='" + featureName + '\'' +
        ", featureStatus='" + featureStatus + '\'' +
        ", totalScenarios=" + totalScenarios +
        ", totalPassed=" + totalPassed +
        ", totalFailed=" + totalFailed +
        '}';
  }
}
