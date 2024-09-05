package com.dummycompany.test.framework.core.context;

import com.dummycompany.test.framework.core.Constants;
import io.cucumber.java.Scenario;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class World {

  private static final Logger LOG = LogManager.getLogger(World.class);
  public static Properties envConfig, proxyConfig;
  public HashMap<String, Object> featureContext;
  public HashMap<String, Object> scenarioContext = new HashMap<>();
  public Scenario scenario;

  /**
   * Logs a message to Cucumber html report as well as to console
   */
  public void log(String message) {
    scenario.log(message);
    if (message.contains(Constants.COMMENT_PREFIX)) {
      LOG.info(message.split(Constants.COMMENT_PREFIX)[1]);
    } else {
      LOG.info(message);
    }
  }

  /**
   * Logs a message to to console
   */
  public void logToConsole(String message) {
    LOG.info(StringEscapeUtils.escapeJava(message));
  }

  /**
   * Please only log information that's very special and will be meaningful in the email report
   *
   * @param commentText
   */
  public void logCommentToEmailableReport(String commentText) {
    log(Constants.COMMENT_PREFIX.concat(commentText));
  }

  public Boolean getBooleanFromScenarioContext(String key) {
    return getObjectFromScenarioContext(key, Boolean.class);
  }

  public Boolean getBooleanFromFeatureContext(String key) {
    return getObjectFromFeatureContext(key, Boolean.class);
  }

  public String getStringFromScenarioContext(String key) {
    return getObjectFromScenarioContext(key, String.class);
  }

  public String getStringFromFeatureContext(String key) {
    return getObjectFromFeatureContext(key, String.class);
  }

  public int getIntFromScenarioContext(String key) {
    return getObjectFromScenarioContext(key, Integer.class);
  }

  public int getIntFromFeatureContext(String key) {
    return getObjectFromFeatureContext(key, Integer.class);
  }

  public long getLongFromScenarioContext(String key) {
    return getObjectFromScenarioContext(key, Long.class);
  }

  public long getLongFromFeatureContext(String key) {
    return getObjectFromFeatureContext(key, Long.class);
  }

  public double getDoubleFromScenarioContext(String key) {
    return getObjectFromScenarioContext(key, Double.class);
  }

  public double getDoubleFromFeatureContext(String key) {
    return getObjectFromFeatureContext(key, Double.class);
  }

  public float getFloatFromScenarioContext(String key) {
    return getObjectFromScenarioContext(key, Float.class);
  }

  public float getFloatFromFeatureContext(String key) {
    return getObjectFromFeatureContext(key, Float.class);
  }

  public <T> T getObjectFromScenarioContext(String key, Class<T> type) {
    try {
      if (scenarioContext.containsKey(key)) {
        return type.cast(scenarioContext.get(key));
      } else {
        throw new IllegalArgumentException("Key not found in Scenario Context. | Key : " + key);
      }
    } catch (Exception e) {
      throw new RuntimeException("Value for given key is null in Scenario Context | Key : " + key);
    }
  }

  public <T> T getObjectFromFeatureContext(String key, Class<T> type) {
    try {
      if (featureContext.containsKey(key)) {
        return type.cast(featureContext.get(key));
      } else {
        throw new IllegalArgumentException("Key not found in Feature Context. | Key :  " + key);
      }
    } catch (Exception e) {
      throw new RuntimeException("Value for given key is null in Feature Context | Key : " + key);
    }
  }
}