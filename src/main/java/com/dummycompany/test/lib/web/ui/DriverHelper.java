package com.dummycompany.test.lib.web.ui;

import com.dummycompany.test.framework.core.Constants;
import com.dummycompany.test.framework.core.utils.Props;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.github.bonigarcia.wdm.config.DriverManagerType;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public class DriverHelper {

  private static final Logger LOG = LogManager.getLogger(DriverHelper.class);
  private static final DesiredCapabilities userProvidedCapabilities = new DesiredCapabilities();
  WebDriver driver = null;

  public SmartRemoteWebDriver launchBrowser() {
    LOG.info("Browser : {}", Constants.BROWSER);
    if (Constants.BROWSER.equalsIgnoreCase("chrome")) {
      driver = startChromeDriver(Constants.PROFILE);
    } else if (Constants.BROWSER.equalsIgnoreCase("firefox")) {
      driver = startFireFoxDriver(Constants.PROFILE);
    } else if(Constants.BROWSER.equalsIgnoreCase("internetexplorer")) {
        driver = startIEDriver(Constants.PROFILE);
    } 
    else {
      throw new IllegalArgumentException("Browser " + Constants.BROWSER + " not supported");
    }
    EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
    if (Props.getEnvOrPropertyValue("enable.selenium.listener", "true").equalsIgnoreCase("true")) {
      eventFiringWebDriver.register(new SeleniumListener());
    }
    SmartRemoteWebDriver smartRemoteWebDriver = new SmartRemoteWebDriver(eventFiringWebDriver);
    smartRemoteWebDriver.manage().timeouts()
        .pageLoadTimeout(Integer.parseInt(Props.getEnvOrPropertyValue("page.load.wait.time", "30")), TimeUnit.SECONDS);
    return smartRemoteWebDriver;
  }

  @SuppressWarnings("deprecation")
  private WebDriver startChromeDriver(String profile) {
    try {
      DesiredCapabilities capabilities = getChromeDesiredCapabilities();
      capabilities.merge(userProvidedCapabilities);
      if (profile.equalsIgnoreCase("local")) {
        if (Props.getEnvOrPropertyValue("driver.auto.download", "true").equalsIgnoreCase("true")) {
          LOG.info("Will auto download chrome web driver");
          try {
            WebDriverManager.getInstance(DriverManagerType.CHROME).setup();
          } catch (Exception e) {
            LOG.error(e);
            String proxyUser = Props.getEnvOrPropertyValue("proxyUser", null);
            String proxyPassword = Props.getEnvOrPropertyValue("proxyPassword", null);
            if (StringUtils.isNotBlank(proxyUser) && StringUtils.isNotBlank(proxyPassword)) {
              String proxyHost = Props.getEnvOrPropertyValue("proxyHostValue", "http-gw.tcif.dummycompany.com.au");
              String proxyPort = Props.getEnvOrPropertyValue("proxyPortValue", "8080");
              String proxyHostAndPort = proxyHost.concat(":").concat(proxyPort);
              try {
                WebDriverManager.getInstance(DriverManagerType.CHROME)
                    .proxy(Props.getEnvOrPropertyValue(proxyHostAndPort, "http-gw.tcif.dummycompany.com.au:8080"))
                    .proxyUser(proxyUser)
                    .proxyPass(proxyPassword)
                    .setup();
              } catch (Exception ex) {
                LOG.warn(ex);
                throw new RuntimeException(
                    "Failed to auto download selenium webDriver chrome binary. Alternately you may download manually and copy to "
                        + "src/test/resources/drivers/chromedriver/win64 and set driver.auto.download=false in src/test/resources/profiles/local/config.properties");

              }
            } else {
              throw new RuntimeException(
                  "Failed to auto download selenium webDriver chrome binary. Supply -DproxyUser=your-dnumber & -DproxyPassword=your-account-01-password and rerun. "
                      + "Alternately you may download manually and copy to "
                      + "src/test/resources/drivers/chromedriver/win64 and set driver.auto.download=false in src/test/resources/profiles/local/config.properties");
            }
          }
        } else {
          LOG.info("Will use existing chrome driver");
          System.setProperty("webdriver.chrome.driver", Props.getEnvOrPropertyValue("driver.chrome.dir"));
        }
        driver = new ChromeDriver(capabilities);
      } else {
        boolean isSingleTest = Boolean.parseBoolean(Objects.requireNonNull(Props.getEnvOrPropertyValue("singleTest", "false")));
        String buildID = Props.getEnvOrPropertyValue("BuildID");
        String applicationName = (isSingleTest) ? buildID + "-SingleTest" : buildID;
        if (Props.getEnvOrPropertyValue("runnerProfile", "junit").equalsIgnoreCase("svt")) {
          capabilities.setCapability("applicationName", applicationName);
        }
        driver = getRemoteWebDriver(capabilities);
      }
      return driver;
    } catch (Exception e) {
      LOG.error("Error while launching Web Driver " + e);
      throw new RuntimeException(e);
    }
  }

  private WebDriver startFireFoxDriver(String profile) {
    FirefoxProfile firefoxProfile = new FirefoxProfile();
    DesiredCapabilities capabilities = getFireFoxDesiredCapabilities();
    capabilities.merge(userProvidedCapabilities);
    firefoxProfile.setPreference("network.proxy.type", 4);
    capabilities.setCapability(FirefoxDriver.PROFILE, firefoxProfile);
    FirefoxOptions firefoxOptions = new FirefoxOptions(capabilities);
      if (profile.equalsIgnoreCase("local")) {
        if (Props.getEnvOrPropertyValue("driver.auto.download", "true").equalsIgnoreCase("true")) {
          LOG.info("Will auto download firefox web driver binary");
          try {
            WebDriverManager.getInstance(DriverManagerType.FIREFOX).setup();
          } catch (Exception e) {
            LOG.error(e);
            String proxyUser = Props.getEnvOrPropertyValue("proxyUser", null);
            String proxyPassword = Props.getEnvOrPropertyValue("proxyPassword", null);
            if (StringUtils.isNotBlank(proxyUser) && StringUtils.isNotBlank(proxyPassword)) {
              String proxyHost = Props.getEnvOrPropertyValue("proxyHostValue", "http-gw.tcif.dummycompany.com.au");
              String proxyPort = Props.getEnvOrPropertyValue("proxyPortValue", "8080");
              String proxyHostAndPort = proxyHost.concat(":").concat(proxyPort);
              try {
                WebDriverManager.getInstance(DriverManagerType.FIREFOX)
                    .proxy(Props.getEnvOrPropertyValue(proxyHostAndPort, "http-gw.tcif.dummycompany.com.au:8080"))
                    .proxyUser(proxyUser)
                    .proxyPass(proxyPassword)
                    .setup();
              } catch (Exception ex) {
                LOG.warn(ex);
                throw new RuntimeException(
                    "Failed to auto download selenium webDriver FIREFOX binary. Alternately you may download manually and copy to "
                        + "src/test/resources/drivers/geckodriver/win32 and set driver.auto.download=false in src/test/resources/profiles/local/config.properties");

              }
            } else {
              throw new RuntimeException(
                  "Failed to auto download selenium webDriver FIREFOX binary. Supply -DproxyUser=your-dnumber & -DproxyPassword=your-account-01-password and rerun. "
                      + "Alternately you may download manually and copy to "
                      + "src/test/resources/drivers/geckodriver/win32 and set driver.auto.download=false in src/test/resources/profiles/local/config.properties");
            }
          }
        } else {
        LOG.info("Will use existing gecko driver");
        System.setProperty("webdriver.gecko.driver", Props.getEnvOrPropertyValue("driver.gecko.dir"));
      }
      driver = new FirefoxDriver(firefoxOptions);
    } else {
      try {
        driver = getRemoteWebDriver(capabilities);
      } catch (Exception e) {
        LOG.error("Error while launching RemoteWebDriver " + e);
        throw new RuntimeException(e);
      }
    }
    clearCookies();
    maximize();
    return driver;
  }

  private DesiredCapabilities getChromeDesiredCapabilities() {
    LoggingPreferences logs = new LoggingPreferences();
    logs.enable(LogType.BROWSER, Level.ALL);
    logs.enable(LogType.CLIENT, Level.ALL);
    logs.enable(LogType.DRIVER, Level.ALL);
    logs.enable(LogType.SERVER, Level.ALL);
    logs.enable(LogType.PERFORMANCE, Level.ALL);

    DesiredCapabilities capabilities = DesiredCapabilities.chrome();
    capabilities.setCapability(CapabilityType.ForSeleniumServer.ENSURING_CLEAN_SESSION, true);
    capabilities.setCapability(CapabilityType.LOGGING_PREFS, logs);

    HashMap<String, Object> chromePrefs = new HashMap<>();
    chromePrefs.put("profile.default_content_settings.popups", 0);
    chromePrefs.put("download.default_directory", Paths.get(Constants.CURRENT_DIR, "target").toString());
    ChromeOptions chromeOptions = new ChromeOptions();
    if (Props.getEnvOrPropertyValue("browser.full.screen.enabled", "false").equalsIgnoreCase("true")) {
      chromeOptions.addArguments("--start-fullscreen");
    } else {
      if (Props.getEnvOrPropertyValue("profile", "local").equalsIgnoreCase("local")) {
        chromeOptions.addArguments("--start-maximized");
      } else {
        chromeOptions.addArguments("--window-size=1920,1080");
      }
    }
    if (Props.getEnvOrPropertyValue("headlessExec", "false").equalsIgnoreCase("true")) {
      chromeOptions.addArguments("--headless");
      LOG.info("Headless execution enabled..!!!");
    }
    chromeOptions.addArguments("--disable-gpu");
    chromeOptions.setExperimentalOption("prefs", chromePrefs);
    capabilities.setCapability("chrome.verbose", false);
    capabilities.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
    return capabilities;
  }

  private DesiredCapabilities getFireFoxDesiredCapabilities() {
    LoggingPreferences logs = new LoggingPreferences();
    logs.enable(LogType.DRIVER, Level.OFF);
    DesiredCapabilities capabilities = new DesiredCapabilities();
    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
    capabilities.setBrowserName("firefox");
    capabilities.setCapability("disable-restore-session-state", true);
    capabilities.setCapability("marionette", true);
    return capabilities;
  }

  public void maximize() {
    driver.manage().window().maximize();
  }

  public void clearCookies() {
    driver.manage().deleteAllCookies();
  }

  private RemoteWebDriver getRemoteWebDriver(DesiredCapabilities capabilities) throws MalformedURLException {
    String seleniumRemoteUrl = Props.getEnvOrPropertyValue("RemoteUrl", null);
    if (StringUtils.isBlank(seleniumRemoteUrl)) {
      String driverHost = Props.getEnvOrPropertyValue("HUBIP", Props.getProp("driverHost"));
      if (null == driverHost) {
        throw new IllegalArgumentException(
            "Profile is ci but valid HUBIP is not supplied. Possibly you are trying local selenium grid. If yes, please set driverHost=localhost in src/test/resources/profiles/ci/config.properties");
      }
      seleniumRemoteUrl = "http://" + driverHost + ":" + Props.getEnvOrPropertyValue("driverPort") + "/wd/hub";
    }
    return new RemoteWebDriver(new URL(seleniumRemoteUrl), capabilities);
  }

  /**
   * User to call this method if they want to set their own selenium capabilities We will then merge them into default capabilities
   * User to supply DesiredCapabilities. This method should be calling in cucumber before hook of Hook.java
   */
  public static void setCustomSeleniumCapabilities(DesiredCapabilities userCapabilities) {
    try {
      DriverHelper.userProvidedCapabilities.merge(userCapabilities);
      LOG.info("User supplied selenium capabilities : {} | Merging them into core capabilities", userCapabilities.asMap());
    } catch (Exception e) {
      LOG.error(e);
      throw new RuntimeException(e);
    }
  }
  
  private WebDriver startIEDriver(String profile) {
    DesiredCapabilities capabilities = DesiredCapabilities.internetExplorer();
    capabilities.setCapability("requireWindowFocus", true);
    capabilities.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
    capabilities.setCapability("ie.ensureCleanSession", true);
    capabilities.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
    InternetExplorerOptions ieOptions = new InternetExplorerOptions();
    ieOptions.setCapability("requireWindowFocus", true);
    ieOptions.setCapability(InternetExplorerDriver.IGNORE_ZOOM_SETTING, true);
    ieOptions.setCapability("ie.ensureCleanSession", true);
    ieOptions.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
    ieOptions.setCapability("unhandledPromptBehavior", "ignore");
    if (profile.equalsIgnoreCase("local")) {
      if (Props.getEnvOrPropertyValue("driver.auto.download", "true").equalsIgnoreCase("true")) {
        LOG.info("Will auto download internet explorer webdriver");
        WebDriverManager.getInstance(DriverManagerType.IEXPLORER).setup();
      } else {
        LOG.info("Will use existing internet explorer driver");
        System.setProperty("webdriver.ie.driver", Props.getProp("driver.ie.dir"));
      }

      this.driver = new InternetExplorerDriver(ieOptions);
    } else {
      try {
        this.driver = this.getRemoteWebDriver(capabilities);
      } catch (Exception var6) {
        LOG.error("Error while launching RemoteWebDriver " + var6);
        throw new RuntimeException(var6);
      }
    }

    this.clearCookies();
    this.maximize();
    return this.driver;
  }

}
