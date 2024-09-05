package com.dummycompany.test.framework.core.splitjson;

import com.dummycompany.test.framework.core.utils.RunnerUtil;

public class Main {

  public static void main(String[] args) {
    System.setProperty("profile", "svt");
    System.setProperty("back.up.local.cucumber.report", "false");
    System.setProperty("env", "dev");
    RunnerUtil.generateCucumberHtmlReport();
  }

}
