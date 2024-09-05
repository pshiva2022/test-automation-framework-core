package com.dummycompany.test.framework.core.rerun;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.dummycompany.test.framework.core.rerun.model.Element;
import com.dummycompany.test.framework.core.rerun.model.Feature;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RemoveFailedScenariosFromJson {

  public static final String FIRST_RUN_CUCUMBER_JSON_FILE = "./target/cucumber-reports/cucumber.json";
  public static final String FIRST_RUN_PASSED_FEATURES_CUCUMBER_JSON_FILE = "./target/cucumber-reports/first-run-passed-features-cucumber.json";
  public static final String RE_RUN_CUCUMBER_JSON_FILE = "./target/cucumber-reports/re-run-cucumber.json";
  public static final String MERGED_CUCUMBER_JSON_FILE = "./target/cucumber-reports/merged-cucumber.json";
  private static final Logger LOG = LogManager.getLogger(RemoveFailedScenariosFromJson.class);

  private RemoveFailedScenariosFromJson() {
  }

  public static void main(String[] args) throws IOException {
    removeFailedScenarios(false);
    mergeFirstRunAndReRunJson();
  }

  public static void removeFailedScenarios(boolean isFeatureWithChainedScenario) {
    LOG.info("isFeatureWithChainedScenario is false. Removing failed scenarios from {}", FIRST_RUN_CUCUMBER_JSON_FILE);
    Feature[] firstRunFeatures;
    try {
      firstRunFeatures = new ObjectMapper().readValue(new File(FIRST_RUN_CUCUMBER_JSON_FILE), Feature[].class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    List<Feature> firstRunPassedFeatures;
    if (isFeatureWithChainedScenario) {
      // Remove all features from json even if at least once scenario is failed in first run
      firstRunPassedFeatures = Arrays.stream(Objects.requireNonNull(firstRunFeatures))
          .filter(feature -> {
            int beforeFilteringSize = feature.getElements().size();
            int afterFilteringSize = (int) feature.getElements()
                .parallelStream()
                .filter(element -> !isScenarioFailed(element)).count();
            return afterFilteringSize == beforeFilteringSize;
          })
          .filter(feature -> feature.getElements().size() > 0)
          .collect(Collectors.toList());
    } else {
      // Remove all failed scenarios from first run
      firstRunPassedFeatures = Arrays.stream(Objects.requireNonNull(firstRunFeatures))
          .peek(feature -> {
            List<Element> passedScenarioList = feature.getElements().parallelStream()
                .filter(element -> !isScenarioFailed(element))
                .collect(Collectors.toList());
            feature.setElements(passedScenarioList);
          })
          .filter(feature -> feature.getElements().size() > 0)
          .collect(Collectors.toList());
    }
    try {
      new ObjectMapper().writeValue(new File(FIRST_RUN_PASSED_FEATURES_CUCUMBER_JSON_FILE), firstRunPassedFeatures);
    } catch (Exception e) {
      LOG.error(e);
      throw new RuntimeException(e);
    }
    LOG.info("Successfully removed failed scenarios from {} and saved to {}", FIRST_RUN_CUCUMBER_JSON_FILE,
        FIRST_RUN_PASSED_FEATURES_CUCUMBER_JSON_FILE);
  }

  private static boolean isScenarioFailed(Element element) {
    boolean isFailed;
    if (element.getBefore() != null) {
      isFailed = element.getBefore().parallelStream()
          .anyMatch(before -> before.getResult().getStatus().equals("failed"));
      if (isFailed) {
        return true;
      }
    }
    if (element.getAfter() != null) {
      isFailed = element.getAfter().parallelStream()
          .anyMatch(after -> after.getResult().getStatus().equals("failed"));
      if (isFailed) {
        return true;
      }
    }
    if (element.getSteps() != null) {
      return element.getSteps().parallelStream()
          .anyMatch(step -> step.getResult().getStatus().equals("failed"));
    }
    return false;
  }

  public static void mergeFirstRunAndReRunJson() {
    LOG.info("Trying to merge first run & re run json");
    Feature[] firstRunFeatures;
    try {
      firstRunFeatures = new ObjectMapper().readValue(new File(FIRST_RUN_PASSED_FEATURES_CUCUMBER_JSON_FILE), Feature[].class);
    } catch (Exception e) {
      LOG.error(e);
      throw new RuntimeException(e);
    }

    Feature[] reRunFeatures;
    try {
      reRunFeatures = new ObjectMapper().readValue(new File(RE_RUN_CUCUMBER_JSON_FILE), Feature[].class);
    } catch (IOException e) {
      LOG.error(e);
      throw new RuntimeException(e);
    }

    Feature[] mergedFeatures = Stream.of(firstRunFeatures, reRunFeatures)
        .flatMap(Stream::of)
        .toArray(Feature[]::new);

    try {
      new ObjectMapper().writeValue(new File(MERGED_CUCUMBER_JSON_FILE), mergedFeatures);
      LOG.info("Successfully merged first run & re run json");
    } catch (Exception e) {
      LOG.error(e);
      throw new RuntimeException(e);
    }
  }

  public static void backUpJson() {
    LOG.info("Backing up json files");
    String sourceDir = "./target/cucumber-reports";
    String destDir = "./target/cucumber-reports-back-up";
    try {
      String[] fileNames = (new File(sourceDir)).list();
      assert fileNames != null;
      for (String fileName : fileNames) {
        if (fileName.contains(".json")) {
          File sourceFile = new File(Paths.get(sourceDir, fileName).toString());
          if (!sourceFile.getName().contains("merged-cucumber.json")) {
            FileUtils.copyFile(sourceFile, new File(Paths.get(destDir, fileName).toString()));
            FileUtils.deleteQuietly(sourceFile);
          }
        }
      }
    } catch (Exception e) {
      LOG.error(e);
      throw new RuntimeException(e);
    }
    LOG.info("Successfully backed up json to {}", destDir);
  }

}

