package com.dummycompany.test.framework.core.utils;

import static org.apache.commons.io.FileUtils.listFiles;

import com.dummycompany.test.framework.core.Constants;
import com.dummycompany.test.framework.core.context.World;
import com.dummycompany.test.framework.core.newreport.NewReport;
import com.dummycompany.test.framework.core.report.HtmlParser;
import com.dummycompany.test.framework.core.rerun.ChainedScenarioUtils;
import com.dummycompany.test.framework.core.rerun.RemoveFailedScenariosFromJson;
import com.dummycompany.test.lib.api.FreemarkerTemplateBuilder;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import net.masterthought.cucumber.Configuration;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.ValidationException;
import net.masterthought.cucumber.reducers.ReducingMethod;
import net.masterthought.cucumber.sorting.SortingMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.ITestContext;


public class RunnerUtil {

  private static final Logger LOG = LogManager.getLogger(RunnerUtil.class);
  public static long START_TIME;
  public static int RPM = 0;

  private RunnerUtil() {
  }

  // Temp change for backward compatibility
  public static Map<String, Object> setUp() {
    return setUp("");
  }

  public static Map<String, Object> setUp(String runnerName) {
    LOG.info("Start of automated test execution...");
    LOG.info("Runner class : {}", runnerName);
    consoleToLogger();
    if (runnerName.equals("TestNGReRunner")) {
      System.setProperty("is.chained.feature", "false");
    }
    Map<String, Object> setUpMap = new HashMap<>();
    LOG.info("Test env : " + StringEscapeUtils.escapeJava(Constants.ENV));
    LOG.info("Profile : " + StringEscapeUtils.escapeJava(Constants.PROFILE));
    Properties envConfig = new Properties();
    Properties proxyConfig = new Properties();
    Props.loadRunConfigProps();
    ClassLoader classLoader;
    Collection<File> files = listFiles(new File(Constants.CURRENT_DIR + "/src/test/java"), new String[]{"properties"}, true);
    boolean isSuppliedEnvValid = files.stream()
        .anyMatch(file -> file.getAbsolutePath().endsWith(Constants.ENV + File.separator + "config.properties"));
    if (!isSuppliedEnvValid) {
      Constants.isValidEnvFound = false;
      LOG.error(StringEscapeUtils.escapeJava(String.format("The supplied env '%s' not found", Constants.ENV)));
      throw new IllegalArgumentException(String.format("The supplied env '%s' not found", Constants.ENV));
    }
    for (File file : files) {
      String absolutePath = file.getAbsolutePath();
      if (absolutePath.endsWith(Constants.ENV + File.separator + "config.properties") || absolutePath
          .endsWith("common" + File.separator + "config.properties") || absolutePath
          .contains("temp" + File.separator + "pom.properties") || absolutePath
          .contains("temp" + File.separator + "execution-time.properties")) {
        try {
          FileInputStream fileInputStream = new FileInputStream(file);
          envConfig.load(fileInputStream);
          fileInputStream.close();
        } catch (Exception e) {
          LOG.error(e);
          throw new RuntimeException(e);
        }
      }
    }
    if (Boolean.parseBoolean(Props.getEnvOrPropertyValue("proxyRequired", "false"))) {
      LOG.info("Proxy Required is True : Proxy being loaded from Config src/test/resources/proxy-config.properties");
      classLoader = Thread.currentThread().getContextClassLoader();

      try {
        InputStream inputStream = Objects.requireNonNull(classLoader).getResourceAsStream("proxy-config.properties");
        proxyConfig.load(inputStream);
        Objects.requireNonNull(inputStream).close();
      } catch (IOException e) {
        LOG.error(e);
        throw new RuntimeException(e);
      }
      proxyConfig.forEach((key, value) -> {
        if (StringUtils.isBlank(System.getProperty(key.toString()))) {
          System.setProperty(key.toString(), value.toString());
        }
      });

      setUpMap.put("proxyConfig", proxyConfig);
    }
    setUpMap.put("envConfig", envConfig);

    envConfig.forEach((key, value) -> {
      if (StringUtils.isBlank(System.getProperty(key.toString()))) {
        System.setProperty(key.toString(), value.toString());
      }
    });
    if (Props.getEnvOrPropertyValue("runnerProfile", "junit").equalsIgnoreCase("svt")) {
      boolean isSingleTest = Boolean.parseBoolean(Props.getEnvOrPropertyValue("singleTest", null));
      String tags = Props.getEnvOrPropertyValue("cucumber.filter.tags", "@generatedFeatureFile");
      if (StringUtils.containsIgnoreCase(tags, "@generatedFeatureFile")) {
        generateFeatureFilesDynamicallyForSVT(isSingleTest);
      }
      List<String> securityGroups = new ArrayList<>();
      securityGroups.add(Props.getEnvOrPropertyValue("SECURITY_GROUP_ID", null));
      String buildId = Props.getEnvOrPropertyValue("BuildID", null);
      int taskCount;
      if (isSingleTest) {
        taskCount = 1;
        buildId = buildId + "-SingleTest";
      } else {
        taskCount = getNodesRequired(Float.parseFloat(Props.getEnvOrPropertyValue("T")), RPM);
      }
    }
    START_TIME = new Date().getTime();
    World.envConfig = (Properties) setUpMap.get("envConfig");
    World.proxyConfig = (Properties) setUpMap.get("proxyConfig");
    return setUpMap;
  }

  /**
   * For testNG runner
   */
  public static void tearDown(boolean shouldCalculateNodesRequired) {
    LOG.info("Runner tear down ...");
    try {
      if (shouldCalculateNodesRequired && Objects.requireNonNull(Props.getEnvOrPropertyValue("cucumber.filter.tags"))
          .equals("@generatedFeatureFile")) {
        RunnerUtil.writeExecutionTimeToPropertyFile();
      }
      RunnerUtil.generateCucumberHtmlReport();
      if (Boolean.parseBoolean(Props.getEnvOrPropertyValue("enableNewHtmlReport", "true"))) {
        NewReport.generateEmailReport(RemoveFailedScenariosFromJson.FIRST_RUN_CUCUMBER_JSON_FILE);
      } else {
        HtmlParser.generateEmailReport();
      }
      // Remove line number for chained scenarios
      if (Props.getEnvOrPropertyValue("RE_RUN_FLAG", "false").equals("true")) {
        LOG.info("RE_RUN_FLAG is true");
        boolean isFeatureWithChainedScenario =
            StringUtils.isNotBlank(System.getProperty("is.chained.feature")) ? Boolean
                .parseBoolean(System.getProperty("is.chained.feature")) : Boolean
                .parseBoolean(ChainedScenarioUtils.getPomProperty("is.chained.feature"));
        ChainedScenarioUtils.removeLineNumbersForChainedScenarios(isFeatureWithChainedScenario);
        RemoveFailedScenariosFromJson.removeFailedScenarios(isFeatureWithChainedScenario);
      }
    } catch (Exception e) {
      LOG.error(ExceptionUtils.getStackTrace(e));
      throw e;
    } finally {
      LOG.info("End of automated test execution...");
    }
    LOG.info("Teardown method completed.");
  }

  /**
   * For junit runner
   */
  public static void tearDown() {
    tearDown(false);
  }

  public static void setThreadCountForTestNGRunner(ITestContext iTestContext, Object[][] scenarios) {
    String threadCountStr = Props.getEnvOrPropertyValue("testNgThreadCount", null);
    int threadCount;
    if (StringUtils.isNotBlank(threadCountStr)) {
      threadCount = Integer.parseInt(threadCountStr);
    } else if (!Constants.PROFILE.equalsIgnoreCase("local")) {
      threadCount = scenarios.length;
    } else {
      threadCount = 1;
    }
    LOG.info("Thread count : {} | Set -DtestNgThreadCount to alter. You may also override this run time via bamboo", threadCount);
    iTestContext.getCurrentXmlTest().getSuite().setDataProviderThreadCount(threadCount);
  }

  public static void generateCucumberHtmlReport() {
    LOG.info("Generating cucumber html reports...");
    List<String> jsonFiles = new ArrayList<>();
    jsonFiles.add(RemoveFailedScenariosFromJson.FIRST_RUN_CUCUMBER_JSON_FILE);
    createReport(jsonFiles);
  }

  public static void teardownForRerun() {
    try {
      LOG.info("Runner tear down for re run ...");
      System.setProperty("isRerun", "true");
      RemoveFailedScenariosFromJson.mergeFirstRunAndReRunJson();
      createReport(Collections.singletonList(RemoveFailedScenariosFromJson.MERGED_CUCUMBER_JSON_FILE));
      RemoveFailedScenariosFromJson.backUpJson();
      if (Boolean.parseBoolean(Props.getEnvOrPropertyValue("enable.new.html.report", "true"))) {
        NewReport.generateEmailReport(RemoveFailedScenariosFromJson.MERGED_CUCUMBER_JSON_FILE);
      } else {
        HtmlParser.generateEmailReport();
      }
    } catch (Exception e) {
      LOG.error(e);
      throw e;
    } finally {
      LOG.info("End of automated test execution...");
    }
  }

  private static void createReport(List<String> jsonFiles) {
    String reportDir;
    boolean shouldBackCucumberReport =
        Props.getEnvOrPropertyValue("back.up.local.cucumber.report", "false").equalsIgnoreCase("true") &&
            Constants.PROFILE.equalsIgnoreCase("local");
    if (shouldBackCucumberReport) {
      reportDir = "cucumber-report/".concat(new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss").format(new Date()));
    } else {
      reportDir = "target";
    }
    LOG.info("Report path directory : " + reportDir);
    File reportOutputDirectory = new File(reportDir);
    String projectName = "Project-Name";
    String buildNumber = "1";
    Configuration configuration = new Configuration(reportOutputDirectory, projectName);
    configuration.setBuildNumber(buildNumber);
    configuration.setSortingMethod(SortingMethod.NATURAL);
    configuration.addReducingMethod(ReducingMethod.MERGE_FEATURES_BY_ID);
    configuration.addClassifications("ENVIRONMENT", Props.getEnvOrPropertyValue("env", "dev"));
    if (StringUtils.isNotBlank(System.getProperty("cucumber.filter.tags"))) {
      configuration.addClassifications("TAGS", System.getProperty("cucumber.filter.tags"));
    }
    if (Props.getEnvOrPropertyValue("RE_RUN_FLAG", "false").equals("true")) {
      configuration.addClassifications("RE_RUN_FLAG", "TRUE");
      configuration.addClassifications("IS_CHAINED_FEATURE", ChainedScenarioUtils.getPomProperty("is.chained.feature"));
    } else {
      configuration.addClassifications("RE_RUN_FLAG", "FALSE");
    }
    try {
      ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
      reportBuilder.generateReports();
      LOG.info("Successfully generated cucumber html reports...");
    } catch (ValidationException e) {
      LOG.warn("Failed to generate cucumber report. Most likely no json found or no scenario was run");
    }
  }

  public static List<String> getAllCucumberJsonFiles() {
    String cucumberJsonDir = Paths.get(Constants.CURRENT_DIR, "target", "cucumber-reports").toString();
    List<String> files = new ArrayList<>();
    String[] fileNames = new File(cucumberJsonDir).list();
    assert fileNames != null;
    for (String fileName : fileNames) {
      if (fileName.contains(".json") && !fileName.equals("cucumber.json")) {
        files.add(Paths.get(cucumberJsonDir, fileName).toString());
      }
    }
    return files;
  }

  public static void generateFeatureFilesDynamicallyForSVT(boolean singleTest) {
    LOG.info("inside generateFeatureFilesDynamicallyForSVT method");
    String featureFileTemplatePath = Props.getEnvOrPropertyValue("FEATURE_FILE_TEMPLATE_PATH");
    String testDataFilePath = Props.getEnvOrPropertyValue("TEST_DATA_FILE_PATH");
    String testDataStrategy;
    int rpm;
    int testDuration;
    int lvp;
    if (singleTest) {
      rpm = 1;
      testDuration = 1;
      lvp = 100;
      testDataStrategy = "sequential";
    } else {
      rpm = Integer.parseInt(Objects.requireNonNull(Props.getEnvOrPropertyValue("RPM")));
      testDuration = Integer.parseInt(Objects.requireNonNull(Props.getEnvOrPropertyValue("TEST_DURATION")));
      lvp = Integer.parseInt(Objects.requireNonNull(Props.getEnvOrPropertyValue("LVP")));
      testDataStrategy = Objects.requireNonNull(Props.getEnvOrPropertyValue("TEST_DATA_USAGE_STRATEGY")).toLowerCase();
      rpm = (int) Math.round(rpm * (lvp / 100.0));
    }
    RPM = rpm;
    LOG.info("calculated RPM is " + RPM);
    int requiredNumberOfScenarios = (rpm * testDuration) + 1;
    try {
      List<String> headers = new ArrayList<>();
      DataFormatter fmt = new DataFormatter();
      List<HashMap<String, String>> data = new ArrayList<>();
      FileInputStream fileInputStream = new FileInputStream(Paths.get(Constants.CURRENT_DIR, testDataFilePath).toFile());
      Workbook workbook = new XSSFWorkbook(fileInputStream);
      Sheet sheet = workbook.getSheetAt(0);
      int rows = sheet.getPhysicalNumberOfRows();
      if (testDataStrategy.equals("unique") && rows < requiredNumberOfScenarios) {
        requiredNumberOfScenarios = rows;
      }
      for (int i = 0, r = 0; i < Math.max(rows, requiredNumberOfScenarios); i++, r++) {
        if (r == rows) {
          r = 1;
        }
        Row row = sheet.getRow(r);
        HashMap<String, String> rowData = new HashMap<>();
        if (row == null) {
          break;
        }
        for (int c = 0; c < row.getLastCellNum(); c++) {
          Cell cell = row.getCell(c);
          String value;
          if (cell != null) {
            value = fmt.formatCellValue(cell);
            if (r == 0) {
              headers.add(value);
            } else {
              rowData.put(headers.get(c), value);
            }
          }
        }
        if (r > 0) {
          data.add(rowData);
        }
      }
      if (testDataStrategy.equalsIgnoreCase("unique")) {
        data.remove(0);
      }
      if (testDataStrategy.equalsIgnoreCase("random")) {
        Collections.shuffle(data);
      }
      data = data.stream().limit(requiredNumberOfScenarios - 1).collect(Collectors.toList());
      HashMap<String, Object> dataMap = new HashMap<>();
      dataMap.put("data", data);
      dataMap.put("rpm", rpm);
      String featureAsString = FreemarkerTemplateBuilder.fromTemplate(featureFileTemplatePath)
          .build(dataMap);
      featureAsString = "@generatedFeatureFile \n" + featureAsString;
      LOG.info("Generated Feature file content :" + featureAsString);
      try (Writer writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(
              Paths.get(Constants.CURRENT_DIR, featureFileTemplatePath.substring(0, featureFileTemplatePath.lastIndexOf(".")))
                  .toFile()),
          StandardCharsets.UTF_8))) {
        writer.write(featureAsString);
      } catch (Exception e) {
        LOG.error("An error happened while writing the generated feature file.");
        LOG.error(e);
        throw e;
      }
    } catch (Exception e) {
      LOG.error(e);
      throw new RuntimeException(e);
    }
  }

  public static void generateFeatureFilesFromExcel(String featureFileTemplatePath, String testDataFilePath) {
    LOG.info("Generating feature files from excel sheet");
    LOG.info("Feature file template path : {}", featureFileTemplatePath);
    LOG.info("Test data excel sheet path : {}", testDataFilePath);
    try {
      List<String> headers = new ArrayList<>();
      DataFormatter fmt = new DataFormatter();
      List<HashMap<String, String>> data = new ArrayList<>();
      FileInputStream fileInputStream = new FileInputStream(Paths.get(testDataFilePath).toFile());
      Workbook workbook = new XSSFWorkbook(fileInputStream);
      Sheet sheet = workbook.getSheetAt(0);
      for (int i = 0, r = 0; i < sheet.getPhysicalNumberOfRows(); i++, r++) {
        Row row = sheet.getRow(r);
        HashMap<String, String> rowData = new HashMap<>();
        if (row == null) {
          break;
        }
        for (int c = 0; c < row.getLastCellNum(); c++) {
          Cell cell = row.getCell(c);
          String value;
          if (cell != null) {
            value = fmt.formatCellValue(cell);
            if (r == 0) {
              headers.add(value);
            } else {
              rowData.put(headers.get(c), value);
            }
          }
        }
        if (r > 0) {
          data.add(rowData);
        }
      }
      HashMap<String, Object> dataMap = new HashMap<>();
      dataMap.put("data", data);
      String featureAsString = FreemarkerTemplateBuilder.fromTemplate(featureFileTemplatePath)
          .build(dataMap);
      featureAsString = "@generatedFeatureFile \n" + featureAsString;
      LOG.info("Generated Feature file content :" + featureAsString);
      try (Writer writer = new BufferedWriter(new OutputStreamWriter(
          new FileOutputStream(
              Paths.get(Constants.CURRENT_DIR, featureFileTemplatePath.substring(0, featureFileTemplatePath.lastIndexOf(".")))
                  .toFile()),
          StandardCharsets.UTF_8))) {
        writer.write(featureAsString);
      } catch (Exception e) {
        LOG.error("An error happened while writing the generated feature file.");
        LOG.error(e);
        throw e;
      }
    } catch (Exception e) {
      LOG.error(e);
      throw new RuntimeException(e);
    }
  }

  private static void writeExecutionTimeToPropertyFile() {
    float timeRequiredInMinutes = (new Date().getTime() - RunnerUtil.START_TIME) / 60000f;
    LOG.info("T in minutes = " + timeRequiredInMinutes);
    if (timeRequiredInMinutes < 1) {
      timeRequiredInMinutes = 1;
    }
    Properties properties = new Properties();
    try (OutputStream outputStream = new FileOutputStream(
        Paths.get(Constants.TEMP_DIR, "execution-time.properties").toFile())) {
      properties.setProperty("T", String.valueOf(timeRequiredInMinutes));
      properties.store(outputStream, "Writing timeRequiredInMinutes required in minutes to execute one scenario");
    } catch (Exception e) {
      LOG.error(e);
      throw new RuntimeException(e);
    }
  }

  private static int getNodesRequired(float timeInMinutes, int RPM) {
    if (timeInMinutes < 5) {
      LOG.info("Using multiplier as 3");
      return (int) Math.ceil((3 * timeInMinutes * RPM) / 5);
    } else {
      LOG.info("Using multiplier as 2");
      return (int) Math.ceil((2 * timeInMinutes * RPM) / 5);
    }
  }

  public static void consoleToLogger(){
    try {
      Level level;
      if (Boolean.parseBoolean(Props.getEnvOrPropertyValue("consoleToLoggerEnabled", "false"))) {
        level = Level.INFO;
      } else {
        level = Level.TRACE;
      }
      System.setOut(new PrintStream(new ConsoleToLogger(LogManager.getLogger("out"), level, System.out), true, "UTF-8"));
    } catch (Exception e) {
      // Do nothing
    }
  }

}
