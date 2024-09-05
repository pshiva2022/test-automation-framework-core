package com.dummycompany.test.framework.core.utils;


import com.dummycompany.test.framework.core.Constants;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Props {

  private static final Logger LOG = LogManager.getLogger(Props.class);
  private static Properties properties;

  /**
   * Gets the key from Config.properties related to chosen profile
   *
   * @param key
   **/
  public static String getProp(String key) {
    if (StringUtils.isBlank(key)) {
      return "";
    } else {
      try {
        return properties.getProperty(key);
      } catch (Exception e) {
        return null;
      }
    }
  }

  @Deprecated
  public static String getSystemProp(String key) {
    throw new UnsupportedOperationException(" *************** Props.getSystemProp(key) is deprecated. Instead use Props.getEnvOrPropertyValue(key) *******************");
  }

  @Deprecated
  public static String setSystemProp(String key, String val) {
    throw new UnsupportedOperationException(" *************** Props.setSystemProp(key, val) is deprecated. Instead use System.setProperty(key, val); *******************");
  }

  public static void loadRunConfigProps() {
    String profile = getEnvOrPropertyValue("profile", "local");
    properties = new Properties();
    try (InputStream inputStream = Props.class.getResourceAsStream("/profiles/" + profile + "/config.properties")) {
      properties.load(inputStream);
      LOG.info("Properties : {}", properties);
    } catch (IOException e) {
      LOG.error(e.getMessage());
    }
  }

  public static String getBrowserName() {
    return Constants.BROWSER;
  }

  public static String getEnvOrPropertyValue(String key, String defaultValue) {
    if (StringUtils.isNotBlank(System.getProperty(key))) {
      return System.getProperty(key);
    } else if (StringUtils.isNotBlank(System.getenv(key))) {
      return System.getenv(key);
    } else if (StringUtils.isNotBlank(getProp(key))) {
      return getProp(key);
    } else {
      return defaultValue;
    }
  }

  public static String getEnvOrPropertyValue(String key) {
    if (StringUtils.isNotBlank(System.getProperty(key))) {
      return System.getProperty(key);
    } else if (StringUtils.isNotBlank(System.getenv(key))) {
      return System.getenv(key);
    } else if (StringUtils.isNotBlank(getProp(key))) {
      return getProp(key);
    } else {
      throw new IllegalArgumentException(
          String.format("Could not find Environment or property variable : '%s'. Please set as appropriate", key));
    }
  }
}
