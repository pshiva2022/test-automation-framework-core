package com.dummycompany.test.framework.core.newreport;

public class ScenarioTableModel {

  private int srNo;
  private String scenarioName;
  private String status;
  private String potentialFailureReason;
  private int stepsPassed;
  private int stepsFailed;
  private int stepsUndefined;

  public int getSrNo() {
    return srNo;
  }

  public void setSrNo(int srNo) {
    this.srNo = srNo;
  }

  public String getScenarioName() {
    return scenarioName;
  }

  public void setScenarioName(String scenarioName) {
    this.scenarioName = scenarioName;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getPotentialFailureReason() {
    return potentialFailureReason;
  }

  public void setPotentialFailureReason(String potentialFailureReason) {
    this.potentialFailureReason = potentialFailureReason;
  }

  public int getStepsPassed() {
    return stepsPassed;
  }

  public void setStepsPassed(int stepsPassed) {
    this.stepsPassed = stepsPassed;
  }

  public int getStepsFailed() {
    return stepsFailed;
  }

  public void setStepsFailed(int stepsFailed) {
    this.stepsFailed = stepsFailed;
  }

  public int getStepsUndefined() {
    return stepsUndefined;
  }

  public void setStepsUndefined(int stepsUndefined) {
    this.stepsUndefined = stepsUndefined;
  }

  @Override
  public String toString() {
    return "ScenarioTableModel{" +
        "srNo=" + srNo +
        ", scenarioName='" + scenarioName + '\'' +
        ", status='" + status + '\'' +
        ", potentialFailureReason='" + potentialFailureReason + '\'' +
        ", stepsPassed=" + stepsPassed +
        ", stepsFailed=" + stepsFailed +
        ", stepsSkipped=" + stepsUndefined +
        '}';
  }
}
