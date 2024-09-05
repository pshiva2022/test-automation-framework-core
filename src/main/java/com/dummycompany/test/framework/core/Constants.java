package com.dummycompany.test.framework.core;

import com.dummycompany.test.framework.core.utils.Props;
import java.nio.file.Paths;
import java.util.HashMap;

public class Constants {

  public static final String CURRENT_DIR = Paths.get(System.getProperty("user.dir")).toString();
  /**
   * A temp directory to generate run time files.
   * This directory is added to git ignore
   * The reason we don't want to save to target folder is that it gets cleaned
   */
  public static final String TEMP_DIR = Paths.get(CURRENT_DIR, "src", "test", "java", "domain", "temp").toString();
  public static ThreadLocal<HashMap<String,Object>> FEATURE_CONTEXT = new ThreadLocal<HashMap<String,Object>>(){};
  public static final String TARGET_DIR = Paths.get(CURRENT_DIR, "target").toString();
  public static final String RESOURCE_DIR = Paths.get(CURRENT_DIR, "src", "test", "resources").toString();
  public static final String PROFILE = Props.getEnvOrPropertyValue("profile", "local");
  public static final String ENV = Props.getEnvOrPropertyValue("env", "dev");
  public static final String BROWSER = Props.getEnvOrPropertyValue("browser", "chrome");
  public static final String RUNNER_PROFILE = Props.getEnvOrPropertyValue("runnerProfile", "junit");
  public static final String COMMENT_PREFIX = "__comm__";
  public static boolean isValidEnvFound = true;
}
