package core.hooks;

import com.dummycompany.test.framework.core.Constants;
import com.dummycompany.test.framework.core.context.World;
import com.dummycompany.test.framework.core.utils.RunnerUtil;
import com.dummycompany.test.framework.core.utils.SVTUtils;

import com.dummycompany.test.lib.web.ui.ScreenshotHelper;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.util.Date;
import java.util.HashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.WebDriver;

import static com.dummycompany.test.framework.core.utils.SVTUtils.isSingleTest;
import static com.dummycompany.test.framework.core.utils.SVTUtils.isSvtProfile;

/**
 * Core cucumber hook
 */
public class CoreHooks {

  private static final Logger LOG = LogManager.getLogger(CoreHooks.class);
  private final World world;

  public CoreHooks(World world) {
    this.world = world;
  }

  @Before(order = 100)
  public void coreCucumberBeforeHook(Scenario scenario) {
    LOG.info("Core cucumber before hook ");
    ThreadContext.put("featureName", getFeatureFileNameFromScenarioId(scenario));
    ThreadContext.put("scenarioName", scenario.getName().concat("-Line:").concat(scenario.getLine().toString()));
    LOG.info("Core cucumber before hook ");
    if (Constants.FEATURE_CONTEXT.get() == null) {
      Constants.FEATURE_CONTEXT.set(new HashMap<>());
    }
    world.featureContext = Constants.FEATURE_CONTEXT.get();
    // Now you can use scenario attribute directly in world object instead of using scenario context.
    //Deprecated: stop using the below statement
    world.scenarioContext.put("scenario", scenario);
    world.scenario = scenario;
    world.scenarioContext.put("execDelay", new Date().getTime() - RunnerUtil.START_TIME);
  }

  @After(order = 100)
  public void coreCucumberAfterHook(Scenario scenario) {
      LOG.info("Core cucumber after hook ");
      if (isSvtProfile && !isSingleTest) {
        if (!world.scenarioContext.containsKey("eventStatus")) {
          world.scenarioContext.put("startTime", (new Date()).getTime());
          world.scenarioContext.put("eventId", 500);
          world.scenarioContext.put("eventStatus", "Test failed before the first event is reached.");
          SVTUtils.stopEventTimer(world, "fail");
        } else if (!world.getStringFromScenarioContext("eventStatus").equalsIgnoreCase("completed")) {
          SVTUtils.stopEventTimer(world, "fail");
        }
      }
      if (world.scenarioContext.containsKey("webDriver") && world.scenarioContext.get("webDriver") != null) {
        WebDriver driver = (WebDriver) world.scenarioContext.get("webDriver");
        if (scenario.isFailed()) {
          ScreenshotHelper.capture(world,driver);
          ScreenshotHelper.captureFullPage(world, driver);
        }
        driver.quit();
        LOG.info("Successfully closed web driver");
      } else {
        LOG.info("No active webDriver instance found to quit. Skipping");
      }
    }

  private String getFeatureFileNameFromScenarioId(Scenario scenario) {
    String scenarioId = scenario.getId();
    int start = scenarioId.lastIndexOf("/") + 1;
    int end = scenarioId.lastIndexOf(".");
    return scenarioId.substring(start, end);
  }

}
