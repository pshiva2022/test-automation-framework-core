package com.dummycompany.test.framework.core.listener;

import com.dummycompany.test.framework.core.Constants;
import com.dummycompany.test.framework.core.utils.RunnerUtil;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.Result;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.TestCase;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CucumberEventListener implements ConcurrentEventListener {

  private static final Logger LOG = LogManager.getLogger(CucumberEventListener.class);

  static {
    if (Constants.RUNNER_PROFILE.equals("junit")) {
      RunnerUtil.consoleToLogger();
    }
  }

  @Override
  public void setEventPublisher(EventPublisher publisher) {
    publisher.registerHandlerFor(TestCaseStarted.class, this::handleTestCaseStarted);
    publisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
  }

  private void handleTestCaseStarted(TestCaseStarted event) {
    TestCase testCase = event.getTestCase();
    LOG.info("Executing Scenario : [{}]", testCase.getName());
  }

  private void handleTestCaseFinished(TestCaseFinished event) {
    TestCase testCase = event.getTestCase();
    Result result = event.getResult();
    Status status = result.getStatus();
    String resultDuration = DurationFormatUtils.formatDurationHMS(result.getDuration().toMillis()).concat("(H:m:s.millis)");
    if (status.name().contains("PASSED")) {
      LOG.info("Scenario : {} | Status : Passed | Duration : [{}]",
          testCase.getName().concat(":").concat(testCase.getLine().toString()), resultDuration);
    } else {
      LOG.error("Scenario : {} | Status : Failed | Duration : [{}] | Failure cause ==> ",
          testCase.getName().concat(":").concat(testCase.getLine().toString()), resultDuration, result.getError());
    }
  }
}

