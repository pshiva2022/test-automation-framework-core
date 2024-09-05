package com.dummycompany.test.framework.core.rerun;

import com.dummycompany.test.framework.core.Constants;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChainedScenarioUtils {

  private static final String SPLIT_STRING = ".feature:";
  private static final String RERUN_FILE = "./target/cucumber-reports/rerun.txt";
  private static Properties properties;
  private static final Logger LOG = LogManager.getLogger(ChainedScenarioUtils.class);

  public static void main(String[] args) {
    removeLineNumbersForChainedScenarios(true);
  }

  private ChainedScenarioUtils() {
  }

  public static void removeLineNumbersForChainedScenarios(boolean isFeatureWithChainedScenario) {
    LOG.info("isFeatureWithChainedScenario is {}", isFeatureWithChainedScenario);
    if (isFeatureWithChainedScenario) {
      LOG.info(
          "Will rerun failed feature file fully even if there was at least one scenario failed. "
              + "If you only want to run failed scenarios and then please set <is.chained.feature>false</is.chained.feature> "
              + "under properties in your pom.xml");
      LOG.info("Will remove line numbers from rerun.txt");
      List<String> rerunList = readRerunFile();
      writeRerunFile(rerunList);
    } else {
      LOG.info(
          "Will only rerun failed scenarios from failed feature files."
              + "If you want to run entire feature file if there is at least one scenario failed then please set "
              + "<is.chained.feature>true</is.chained.feature> under properties in your pom.xml");
      LOG.info("Will not remove line numbers from rerun.txt");
    }
  }

  private static List<String> readRerunFile() {
    LOG.info("Chained scenario flag is true. Will remove line numbers");
    String strLine = "";
    List<String> rerunList = new ArrayList<>();
    try {
      BufferedReader reader = new BufferedReader(new FileReader(RERUN_FILE));
      while (strLine != null) {
        strLine = reader.readLine();
        if (strLine != null) {
          strLine = strLine.split(SPLIT_STRING)[0].concat(SPLIT_STRING);
          rerunList.add(strLine);
        }
      }
      reader.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    LOG.info("Successfully removed line numbers from rerun.txt");
    LOG.info(rerunList);
    return rerunList;
  }

  private static void writeRerunFile(List<String> list) {
    BufferedWriter bufferedWriter;
    try {
      bufferedWriter = new BufferedWriter(new FileWriter(RERUN_FILE));
      for (String var : list) {
        bufferedWriter.write(var);
        bufferedWriter.newLine();
      }
      bufferedWriter.close();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    LOG.info("Successfully wrote back modified rerun.txt file");
  }

  public static synchronized String getPomProperty(String propertyName) {
    if (properties == null) {
      try {
        properties = new Properties();
        FileInputStream fileInputStream = FileUtils.openInputStream(Paths.get(Constants.TEMP_DIR, "pom.properties").toFile());
        properties.load(fileInputStream);
        fileInputStream.close();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return properties.getProperty(propertyName);
  }

}
