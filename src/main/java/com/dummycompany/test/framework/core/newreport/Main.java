package com.dummycompany.test.framework.core.newreport;

import com.dummycompany.test.framework.core.rerun.RemoveFailedScenariosFromJson;

class Main {

  public static void main(String[] args) {
    NewReport.generateEmailReport(RemoveFailedScenariosFromJson.FIRST_RUN_CUCUMBER_JSON_FILE);
  }

}
