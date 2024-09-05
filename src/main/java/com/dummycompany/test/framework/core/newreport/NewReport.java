package com.dummycompany.test.framework.core.newreport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.dummycompany.test.framework.core.Constants;
import com.dummycompany.test.framework.core.rerun.model.Element;
import com.dummycompany.test.framework.core.rerun.model.Feature;
import com.dummycompany.test.framework.core.rerun.model.FeatureTags;
import com.dummycompany.test.framework.core.utils.Props;
import freemarker.template.Template;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NewReport {

  private static final Logger LOGGER = LogManager.getLogger(NewReport.class);

  private NewReport() {
  }

  public static void generateEmailReport(String cucumberFilePath) {
    LOGGER.info("Will generate new email report");
    List<DetailedStatusTableModel> detailedStatusTableModels = new LinkedList<>();
    List<SummaryTableModel> summaryTableModels = new LinkedList<>();
    if (new File(cucumberFilePath).exists()) {
      try {
        List<Feature> featureList = Arrays.stream(Objects.requireNonNull(new ObjectMapper().readValue(new File(cucumberFilePath),
            Feature[].class))).collect(Collectors.toList());

        List<Feature> distinctFeatureList = featureList.stream()
            .collect(Collectors.groupingBy(Feature::getName))
            .values()
            .stream()
            .flatMap(Collection::stream)
            .distinct()
            .collect(Collectors.toList());

        AtomicInteger featureCounter = new AtomicInteger(0);

        distinctFeatureList.forEach(feature -> {
          DetailedStatusTableModel detailedStatusTableModel = new DetailedStatusTableModel();
          detailedStatusTableModel.setSrNo(featureCounter.incrementAndGet());
          detailedStatusTableModel.setFeatureName(feature.getName());
          detailedStatusTableModel.setFeatureDescription(feature.getDescription());
          detailedStatusTableModel.setFeatureFilePath(feature.getUri());
          detailedStatusTableModel.setFeatureTags(
              feature.getTags().stream().map(FeatureTags::getName).collect(Collectors.toList()).toString());

          List<Element> elementList = feature.getElements().stream()
              .filter(element -> element.getType().equals("scenario"))
              .collect(Collectors.toList());

          AtomicInteger scenarioCounter = new AtomicInteger(0);

          List<ScenarioTableModel> currentFeatureScenarioTableModelList = elementList.stream()
              .map(element -> {
                ScenarioTableModel scenarioTableModel = new ScenarioTableModel();
                scenarioTableModel.setSrNo(scenarioCounter.incrementAndGet());
                scenarioTableModel.setScenarioName(element.name + ":" + element.getLine());
                scenarioTableModel.setStatus(getCurrentScenarioStatus(element));
                scenarioTableModel.setStepsPassed(
                    (int) element.getSteps().stream().filter(step -> step.getResult().getStatus().equals("passed")).count());
                scenarioTableModel.setStepsFailed(
                    (int) element.getSteps().stream().filter(step -> step.getResult().getStatus().equals("failed")).count());
                scenarioTableModel.setStepsUndefined(
                    (int) element.getSteps().stream().filter(step -> step.getResult().getStatus().equals("undefined")).count());
                long commentCount = element.getSteps().stream().filter(step -> step.getOutput() != null).count();
                List<String> commentsList = new LinkedList<>();
                if (commentCount > 0) {
                  element.getSteps().stream()
                      .filter(step -> step.getOutput() != null)
                      .forEach(step -> step.getOutput()
                          .forEach(commentText -> {
                                if (StringUtils.isNotBlank(commentText) && commentText.contains(Constants.COMMENT_PREFIX)) {
                                  commentsList.add(commentText.split(Constants.COMMENT_PREFIX)[1]);
                                }
                              }
                          ));
                }
                scenarioTableModel.setPotentialFailureReason(commentsList.toString());
                return scenarioTableModel;
              }).collect(Collectors.toList());

          detailedStatusTableModel.setScenarioTableModels(currentFeatureScenarioTableModelList);
          detailedStatusTableModels.add(detailedStatusTableModel);

          int totalScenarioCountForCurrentFeature = currentFeatureScenarioTableModelList.size();
          int passedScenarioCountForCurrentFeature = (int) currentFeatureScenarioTableModelList.stream()
              .filter(scenarioTableModel -> scenarioTableModel.getStatus().equals("Passed")).count();
          int failedScenarioCountForCurrentFeature = totalScenarioCountForCurrentFeature - passedScenarioCountForCurrentFeature;

          SummaryTableModel summaryTableModel = new SummaryTableModel();
          summaryTableModel.setSrNo(featureCounter.get());
          summaryTableModel.setFeatureName(feature.getName());
          summaryTableModel.setFeatureStatus(getCurrentFeatureStatus(feature));
          summaryTableModel.setTotalScenarios(totalScenarioCountForCurrentFeature);
          summaryTableModel.setTotalPassed(passedScenarioCountForCurrentFeature);
          summaryTableModel.setTotalFailed(failedScenarioCountForCurrentFeature);
          summaryTableModels.add(summaryTableModel);
        });
      } catch (Exception e) {
        LOGGER.warn("Failed to generate new emailable report. Possibly empty json file due to no scenario executed");
        LOGGER.error(ExceptionUtils.getStackTrace(e));
      }

      try {
        freemarker.template.Configuration configuration = GetFreeMarkerConfiguration.getConfiguration();
        Map<String, Object> templateMap = new HashMap<>();
        String allureReportUrl = StringUtils.isNotBlank(Props.getEnvOrPropertyValue("PROJECT_ID", null)) ?
            "https://allure-ui-davinci.sdpamp.com/allure-docker-service-ui/projects/" + Props.getEnvOrPropertyValue("PROJECT_ID")
                + "/reports/latest" : "https://allure-ui-davinci.sdpamp.com/allure-docker-service-ui/projects";
        templateMap.put("allureReportUrl", allureReportUrl);
        templateMap.put("projectName", Props.getEnvOrPropertyValue("projectName", "My Project"));
        templateMap.put("buildId", Props.getEnvOrPropertyValue("BuildID", "local"));
        templateMap.put("date", LocalDateTime.now().toString());
        templateMap.put("environment", Constants.ENV);
        templateMap.put("summaryTableHeading", "SUMMARY -");
        templateMap.put("summaryTable", summaryTableModels);
        templateMap.put("allScenarioCount", summaryTableModels.stream().mapToInt(SummaryTableModel::getTotalScenarios).sum());
        templateMap.put("allPassedScenarioCount", summaryTableModels.stream().mapToInt(SummaryTableModel::getTotalPassed).sum());
        templateMap.put("allFailedScenarioCount", summaryTableModels.stream().mapToInt(SummaryTableModel::getTotalFailed).sum());
        templateMap.put("detailedStatusTableHeading", "DETAILED STATUS -");
        templateMap.put("detailedStatusTable", detailedStatusTableModels);
        templateMap.put("showDetailedStatusTable", Props.getEnvOrPropertyValue("showDetailedStatusTable", "true"));
        templateMap.put("showFeatureMetaData", Props.getEnvOrPropertyValue("showFeatureMetaData", "true"));
        Template template = configuration.getTemplate("report-template.ftlh");
        final String REPORT_FILE = "target/featureReport.html";
        Writer file = new FileWriter(REPORT_FILE);
        template.process(templateMap, file);
        convertReportToSingleLine(REPORT_FILE);
        LOGGER.info("Successfully generated new email report : {}", REPORT_FILE);
      } catch (Exception exception) {
        LOGGER.error(ExceptionUtils.getStackTrace(exception));
      }
    } else {
      LOGGER.warn("{} was not found. Possibly no scenario was run", cucumberFilePath);
    }
  }

  private static String getCurrentScenarioStatus(Element element) {
    if (isBeforeHookPassed(element) && isAllStepsPassed(element) && isAfterHookPassed(element)) {
      return "Passed";
    } else {
      return "Failed";
    }
  }

  private static boolean isBeforeHookPassed(Element element) {
    try {
      return element.getBefore().stream().anyMatch(before -> before.getResult().getStatus().equals("passed"));
    } catch (Exception e) {
      return false;
    }
  }

  private static boolean isBackGroundPassed(Element element) {
    try {
      if (element.getType().equals("background")) {
        return element.getSteps().stream().anyMatch(step -> step.getResult().getStatus().equals("passed"));
      } else {
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

  private static boolean isAfterHookPassed(Element element) {
    try {
      return element.getAfter().stream().anyMatch(after -> after.getResult().getStatus().equals("passed"));
    } catch (Exception e) {
      return false;
    }
  }

  private static boolean isAllStepsPassed(Element element) {
    try {
      return element.getSteps().stream().anyMatch(step -> step.getResult().getStatus().equals("passed"));
    } catch (Exception e) {
      return false;
    }
  }

  private static void convertReportToSingleLine(String reportFilePath) {
    try {
      String line;
      File reportFile = new File(reportFilePath);
      FileReader reader = new FileReader(reportFile);
      BufferedReader br = new BufferedReader(reader);
      StringBuilder str = new StringBuilder();
      while ((line = br.readLine()) != null) {
        str.append(line);
      }
      FileUtils.writeStringToFile(new File(reportFilePath), str.toString(), StandardCharsets.UTF_8);
      br.close();
      reader.close();
    } catch (Exception e) {
      //
    }
  }

  private static String getCurrentFeatureStatus(Feature feature) {
    long failedScenarios = feature.getElements().stream().filter(element -> !getCurrentScenarioStatus(element).equals("Passed"))
        .count();
    return failedScenarios > 0 ? "Failed" : "Passed";
  }

}
