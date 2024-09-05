package com.dummycompany.test.lib.web.ui;

import com.dummycompany.test.framework.core.utils.Props;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.ElementNotSelectableException;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class PageActions {

  private final long DRIVER_WAIT_TIME = Integer.parseInt(Props.getEnvOrPropertyValue("driver.wait.time", "30"));
  private final Logger LOG = LogManager.getLogger(PageActions.class);
  private String parentWindowHandle = "";
  public WebDriverWait wait;
  public WebDriver webDriver;

  public PageActions(WebDriver driver) {
    webDriver = driver;
    wait = new WebDriverWait(webDriver, DRIVER_WAIT_TIME);
  }

  /**
   * Fluent wait method to wait for elements based on the specified condition
   * <p>
   * //@param webdriver instance
   *
   * @return Fluent wait object
   */
  public FluentWait<WebDriver> getFluentWait() {
    return getFluentWait(DRIVER_WAIT_TIME);
  }

  /**
   * Fluent wait method to wait for elements based on the specified condition
   * <p>
   * //@param webdriver instance
   *
   * @return Fluent wait object
   */
  public FluentWait<WebDriver> getFluentWait(long waitTime) {
    return new FluentWait<>(webDriver).pollingEvery(Duration.ofSeconds(1))
        .withTimeout(Duration.ofSeconds(waitTime))
        .ignoring(StaleElementReferenceException.class)
        .ignoring(NoSuchElementException.class)
        .ignoring(TimeoutException.class)
        .ignoring(ElementNotSelectableException.class)
        .ignoring(ElementNotVisibleException.class)
        .ignoring(ElementNotInteractableException.class);
  }

  /**
   * Returns the current Url from page
   **/
  public String getCurrentUrl() {
    return webDriver.getCurrentUrl();
  }

  /**
   * Returns the current page title from page
   */
  public String getCurrentPageTitle() {
    return webDriver.getTitle();
  }

  /**
   * An expectation for checking the title of a page.
   *
   * @param title the expected title, which must be an exact match
   * @return true when the title matches, false otherwise
   */
  public boolean checkPageTitle(String title) {
    try {
      return getFluentWait().until(ExpectedConditions.titleIs(title));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * An expectation for checking that the title contains a case-sensitive substring
   *
   * @param title the fragment of title expected
   * @return true when the title matches, false otherwise
   */
  public boolean checkPageTitleContains(String title) {
    try {
      return getFluentWait().until(ExpectedConditions.titleContains(title));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * An expectation for the URL of the current page to be a specific url.
   *
   * @param url the url that the page should be on
   * @return <code>true</code> when the URL is what it should be
   */
  public boolean checkPageUrlToBe(String url) {
    try {
      return getFluentWait().until(ExpectedConditions.urlToBe(url));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * An expectation for the URL of the current page to contain specific text.
   *
   * @param fraction the fraction of the url that the page should be on
   * @return <code>true</code> when the URL contains the text
   */
  public boolean checkPageUrlContains(String fraction) {
    try {
      return getFluentWait().until(ExpectedConditions.urlContains(fraction));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Expectation for the URL to match a specific regular expression
   *
   * @param regex the regular expression that the URL should match
   * @return <code>true</code> if the URL matches the specified regular expression
   */

  public boolean checkPageUrlMatches(String regex) {
    try {
      return getFluentWait().until(ExpectedConditions.urlMatches(regex));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Find the dynamic element wait until its visible
   *
   * @param by Element location found by css, xpath, id etc...
   **/
  public WebElement waitForExpectedElement(final By by) {
    return visibilityOfElementLocated(by);
  }

  /**
   * Find the dynamic element wait until its visible
   *
   * @param by Element location found by css, xpath, id etc...
   **/
  public List<WebElement> waitForExpectedElements(final By by) {
    return (visibilityOfAllElementsLocatedBy(by));
  }

  /**
   * Find the dynamic element wait until its visible for a specified time
   *
   * @param by                Element location found by css, xpath, id etc...
   * @param waitTimeInSeconds max time to wait until element is visible
   **/

  public WebElement waitForExpectedElement(final By by, long waitTimeInSeconds) {
    try {
      return visibilityOfElementLocated(by, waitTimeInSeconds);
    } catch (NoSuchElementException | TimeoutException e) {
      LOG.error(e);
      return null;
    }
  }

  public WebElement waitTillElementClickable(final By by) {
    try {
      return getFluentWait().until(ExpectedConditions.elementToBeClickable(by));
    } catch (Exception e) {
      LOG.error(e);
      return null;
    }
  }

  public void jsExecute(final By by) {
    WebElement element = webDriver.findElement(by);
    JavascriptExecutor ex = (JavascriptExecutor) webDriver;
    ex.executeScript("arguments[0].click()", element);
  }

  public void waitTillSpinnerDisappears(final By by) {
    try {
      getFluentWait().until(ExpectedConditions.visibilityOfElementLocated(by));
      getFluentWait().until(ExpectedConditions.invisibilityOfElementLocated(by));
    } catch (Exception e) {
      LOG.error(e);
    }
  }

  public WebElement visibilityOfElementLocated(final By by) {
    return getFluentWait().until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  public WebElement visibilityOfElementLocated(final By by, long waitTimeInSeconds) {
    return getFluentWait(waitTimeInSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
  }

  public String getText(final By by) {
    return waitForExpectedElement(by).getText();
  }

  public String getText(WebElement element) {
    return element.getText();
  }

  public String getAttributeValue(final By by, String value) {
    return waitForExpectedElement(by).getAttribute(value);
  }

  /**
   * An expectation for checking if the given text is present in the specified element.
   *
   * @param element the WebElement
   * @param text    to be present in the element
   * @return true once the element contains the given text
   */
  public boolean textToBePresentInElement(WebElement element, String text) {
    try {
      return getFluentWait().until(ExpectedConditions.textToBePresentInElement(element, text));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * An expectation for checking if the given text is present in the element that matches the given locator.
   *
   * @param by   used to find the element
   * @param text to be present in the element found by the locator
   * @return true once the first element located by locator contains the given text
   */
  public boolean textToBePresentInElementLocated(final By by, final String text) {
    try {
      return getFluentWait().until(ExpectedConditions.textToBePresentInElementLocated(by, text));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * An expectation for checking if the given text is present in the specified elements value attribute.
   *
   * @param element the WebElement
   * @param text    to be present in the element's value attribute
   * @return true once the element's value attribute contains the given text
   */
  public boolean textToBePresentInElementValue(final WebElement element, final String text) {
    try {
      return getFluentWait().until(ExpectedConditions.textToBePresentInElementValue(element, text));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * An expectation for checking if the given text is present in the specified elements value attribute.
   *
   * @param by   used to find the element
   * @param text to be present in the value attribute of the element found by the locator
   * @return true once the value attribute of the first element located by locator contains the given text
   */
  public boolean textToBePresentInElementValue(final By by, final String text) {
    try {
      return getFluentWait().until(ExpectedConditions.textToBePresentInElementValue(by, text));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * An expectation for checking whether the given frame is available to switch to.
   * <p>
   * If the frame is available it switches the given driver to the specified frame.
   *
   * @param frameLocator used to find the frame (id or name)
   */
  public WebDriver frameToBeAvailableAndSwitchToIt(final String frameLocator) {
    return getFluentWait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(frameLocator));
  }

  /**
   * An expectation for checking whether the given frame is available to switch to.
   * <p>
   * If the frame is available it switches the given driver to the specified frame.
   *
   * @param by used to find the frame
   */
  public WebDriver frameToBeAvailableAndSwitchToIt(final By by) {
    return getFluentWait().until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(by));
  }

  /**
   * An expectation for checking that an element is either invisible or not present on the DOM.
   *
   * @param by used to find the element
   */
  public boolean invisibilityOfElementLocated(By by) {
    try {
      return getFluentWait().until(ExpectedConditions.invisibilityOfElementLocated(by));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * An expectation for checking that an element with text is either invisible or not present on the DOM.
   *
   * @param by   used to find the element
   * @param text of the element
   */
  public boolean invisibilityOfElementWithText(final By by, final String text) {
    try {
      return getFluentWait().until(ExpectedConditions.invisibilityOfElementWithText(by, text));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * An expectation for checking an element is visible and enabled such that you can click it.
   *
   * @param by used to find the element
   * @return the WebElement once it is located and clickable (visible and enabled)
   */
  public WebElement elementToBeClickable(By by) {
    return getFluentWait().until(ExpectedConditions.elementToBeClickable(by));
  }

  public WebElement elementToBeClickable(By by, long waitTime) {
    return getFluentWait().until(ExpectedConditions.elementToBeClickable(by));
  }

  /**
   * An expectation for checking an element is visible and enabled such that you can click it.
   *
   * @param element the WebElement
   * @return the (same) WebElement once it is clickable (visible and enabled)
   */

  public WebElement elementToBeClickable(final WebElement element) {
    return getFluentWait().until(ExpectedConditions.elementToBeClickable(element));
  }

  /**
   * Wait until an element is no longer attached to the DOM.
   *
   * @param element The element to wait for.
   * @return false is the element is still attached to the DOM, true otherwise.
   */
  public boolean stalenessOf(final WebElement element) {
    try {
      return getFluentWait().until(ExpectedConditions.stalenessOf(element));
    } catch (Exception e) {
      return false;
    }
  }

  public boolean stalenessOf(final WebElement element, long waitTime) {
    try {
      return getFluentWait().until(ExpectedConditions.stalenessOf(element));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Click on the element
   *
   * @param by The element to wait for.
   * @return nothing.
   */
  public void click(final By by) {
    WebElement webElement;
    try {
      webElement = elementToBeClickable(by);
    }catch (Exception e){
      LOG.warn(e);
      webElement = webDriver.findElement(by);
    }
    try {
      click(webElement);
    } catch (StaleElementReferenceException e) {
      LOG.warn("StaleElementReferenceException was thrown. Trying to click again | {}", by.toString());
      click(webElement);
    }
  }

  public void click(final WebElement element) {
    try {
      element.click();
    } catch (ElementNotInteractableException exception){
      LOG.warn("{} was thrown. Scrolling into view | {}", exception.getMessage(), element.toString());
      scrollIntoView_Element(element);
      try {
        LOG.info("Trying to click again after scrolling | {}", element.toString());
        elementToBeClickable(element).click();
      } catch (Exception e) {
        LOG.warn(e);
        LOG.info("Couldn't click using click(). Trying using jsClick() | {}", element.toString());
        JavascriptExecutor js = (JavascriptExecutor) webDriver;
        js.executeScript("var ele = arguments[0];ele.addEventListener('click', function() {ele.setAttribute('automationTrack','true');});ele.click();", element);
        try{
          if(!Boolean.parseBoolean(element.getAttribute("automationTrack"))){
            LOG.error("Couldn't click using js click. automationTrack attribute is not true | {}", element.toString());
            LOG.error(e);
            throw e;
          }
          else{
            LOG.info("Js click worked as automationTrack attribute is set to true | {}", element.toString());
          }
        }catch(Exception ex){
          LOG.info("An exception happened while getting automationTrack attribute. if this is a StaleElementException then js click has worked. | {}", element.toString());
          LOG.warn(ex);
        }
      }
    }
  }

  /*
   * to double click on element
   *
   */
  public void doubleclick(final By by) {
    Actions action = new Actions(webDriver);
    waitForExpectedElement(by);
    action.moveToElement(webDriver.findElement(by)).doubleClick().perform();
  }

  // D797548-Ajay
  public void OpenURLinSeparateTAB(String url) {
    String selectLinkOpeninNewTab = Keys.chord(Keys.CONTROL, Keys.RETURN);
    webDriver.findElement(By.linkText(url)).sendKeys(selectLinkOpeninNewTab);
  }

  public void moveToElementAndClick(By by) {
    WebElement element = webDriver.findElement(by);
    Actions actions = new Actions(webDriver);
    actions.moveToElement(element).click().perform();
  }

  /**
   * An expectation for checking if the given element is selected.
   */
  public boolean elementToBeSelected(final By by) {
    try {
      return getFluentWait().until(ExpectedConditions.elementToBeSelected(by));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * An expectation for checking if the given element is selected.
   */
  public boolean elementToBeSelected(final WebElement element) {
    try {
      return getFluentWait().until(ExpectedConditions.elementToBeSelected(element));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * An expectation for checking if the given element is selected.
   */
  public boolean elementSelectionStateToBe(final WebElement element, final boolean selected) {
    try {
      return getFluentWait().until(ExpectedConditions.elementSelectionStateToBe(element, selected));
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * An expectation for checking if the given element is selected.
   */
  public boolean elementSelectionStateToBe(final By by, final boolean selected) {
    try {
      return getFluentWait().until(ExpectedConditions.elementSelectionStateToBe(by, selected));
    } catch (Exception e) {
      return false;
    }
  }

  public void waitForAlert() {
    getFluentWait().until(ExpectedConditions.alertIsPresent());
  }

  /**
   * An expectation for checking that all elements present on the web page that match the locator are visible. Visibility means that
   * the elements are not only displayed but also have a height and width that is greater than 0.
   *
   * @param by used to find the element
   * @return the list of WebElements once they are located
   */
  public List<WebElement> visibilityOfAllElementsLocatedBy(final By by) {
    return getFluentWait().until(ExpectedConditions.visibilityOfAllElementsLocatedBy(by));
  }

  /**
   * An expectation for checking that all elements present on the web page that match the locator are visible. Visibility means that
   * the elements are not only displayed but also have a height and width that is greater than 0.
   *
   * @param elements list of WebElements
   * @return the list of WebElements once they are located
   */
  public List<WebElement> visibilityOfAllElements(final List<WebElement> elements) {
    return getFluentWait().until(ExpectedConditions.visibilityOfAllElements(elements));
  }

  /**
   * An expectation for checking that there is at least one element present on a web page.
   *
   * @param by used to find the element
   * @return the list of WebElements once they are located
   */
  public List<WebElement> presenceOfAllElementsLocatedBy(final By by) {
    return getFluentWait().until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
  }

  /**
   * An expectation for checking that an element, known to be present on the DOM of a page, is visible. Visibility means that the
   * element is not only displayed but also has a height and width that is greater than 0.
   *
   * @param element the WebElement
   * @return the (same) WebElement once it is visible
   */

  public WebElement visibilityOf(final WebElement element) {
    return getFluentWait().until(ExpectedConditions.visibilityOf(element));
  }

  /**
   * An expectation for checking that an element is present on the DOM of a page. This does not necessarily mean that the element is
   * visible.
   *
   * @param by used to find the element
   * @return the WebElement once it is located
   */
  public boolean isElementPresent(final By by) {
    try {
      getFluentWait().until(ExpectedConditions.presenceOfElementLocated(by));
    } catch (TimeoutException exception) {
      return false;
    }
    return true;
  }

  public boolean isElementPresent(final By by, long waitTime) {
    try {
      getFluentWait(waitTime).until(ExpectedConditions.presenceOfElementLocated(by));
    } catch (TimeoutException exception) {
      return false;
    }
    return true;
  }

  public boolean isElementVisible(final By by) {
    try {
      getFluentWait().until(ExpectedConditions.visibilityOfElementLocated(by));
    } catch (TimeoutException exception) {
      return false;
    }
    return true;
  }

  public boolean isElementVisible(final By by, long waitTime) {
    try {
      getFluentWait(waitTime).until(ExpectedConditions.visibilityOfElementLocated(by));
    } catch (TimeoutException exception) {
      return false;
    }
    return true;
  }

  /**
   * An expectation for checking that an element is displayed and enabled on the DOM of a page.
   *
   * @param by used to find the element
   * @return the WebElement once it is located
   */
  public boolean isElementDisplayedAndEnabled(final By by) {
    try {
      try {
        WebElement webElement = waitForExpectedElement(by);
        return webElement.isDisplayed() && webElement.isEnabled();
      } catch (StaleElementReferenceException exception) {
        WebElement element = elementToBeClickable(by);
        return null != element;
      }
    } catch (Exception exception) {
      return false;
    }
  }

  public boolean isElementDisplayedAndEnabled(final By by, long waitTime) {
    try {
      try {
        WebElement webElement = waitForExpectedElement(by, waitTime);
        return webElement.isDisplayed()
            && webElement.isEnabled();
      } catch (StaleElementReferenceException exception) {
        LOG.error(exception);
        WebElement element = elementToBeClickable(by, waitTime);
        return null != element;
      }
    } catch (Exception exception) {
      return false;
    }

  }

  public WebDriver getBrowserByPageTitle(String pageTitle) {
    for (String windowHandle : webDriver.getWindowHandles()) {
      webDriver = webDriver.switchTo().window(windowHandle);
      if (pageTitle.contains(webDriver.getTitle())) {

        return webDriver;
      }
    }
    return null;
  }

  public void switchToParentWindow() {
    webDriver.switchTo().window(parentWindowHandle);
  }

  public void switchToWindow() {
    parentWindowHandle = webDriver.getWindowHandle();
    Set<String> windowHandles = webDriver.getWindowHandles();
    for (String window : windowHandles) {
      if (!window.equals(parentWindowHandle)) {
        webDriver.switchTo().window(window);
        LOG.info("Switching to lookup window.. " + webDriver.getTitle());
      }
    }
  }

  public void navigateToPreviousPageUsingBrowserBackButton() {
    webDriver.navigate().back();
  }

  public void clickWithinElementWithXYCoordinates(WebElement webElement, int x, int y) {
    Actions builder = new Actions(webDriver);
    builder.moveToElement(webElement, x, y);
    builder.click();
    builder.perform();
  }

  public void hoverAndClickOnElement(final By by) {
    Actions builder = new Actions(webDriver);
    builder.moveToElement(waitForExpectedElement(by));
    builder.click();
    builder.build().perform();
  }

  public void clickUsingActions(final By by) {
    WebElement el = webDriver.findElement(by);
    Actions builder = new Actions(webDriver);
    builder.moveToElement(el);
    builder.sendKeys(el, Keys.RETURN);
    builder.build().perform();

  }

  public void hoverWebelement(final By by) {
    Actions builder = new Actions(webDriver);
    builder.moveToElement(waitForExpectedElement(by));
    builder.build().perform();
  }

  public void hoverAndClickOnElement(WebElement webElement) {
    Actions builder = new Actions(webDriver);
    builder.moveToElement(webElement);
    builder.click();
    builder.build().perform();
  }

  public String getElementByTagNameWithJSExecutor(String tagName) {
    return ((JavascriptExecutor) webDriver)
        .executeScript("return window.getComputedStyle(document.getElementsByTagName('" + tagName + "')")
        .toString();
  }

  public String getElementByQueryJSExecutor(String cssSelector) {
    return ((JavascriptExecutor) webDriver)
        .executeScript("return window.getComputedStyle(document.querySelector('" + cssSelector + "')")
        .toString();
  }

  /**
   * Wrapper for clear data and sendKeys in Input Text box
   *
   * @param by        Element location found by css, xpath, id etc...
   * @param inputText text to be entered
   **/

  public void clearEnterText(By by, String inputText) {
    waitForExpectedElement(by).clear();
    waitForExpectedElement(by).sendKeys(inputText);
  }

  public void clickEnter(By by, Keys inputText) {

    waitForExpectedElement(by).sendKeys(inputText);
  }

  public void clickEnter(By by, Keys inputText, Keys inputText1) {

    waitForExpectedElement(by).sendKeys(inputText);
    waitForExpectedElement(by).sendKeys(inputText1);

  }

  public void clickEnter(By by, Keys inputText, Keys inputText1, Keys inputText2) {

    waitForExpectedElement(by).sendKeys(inputText);
    waitForExpectedElement(by).sendKeys(inputText1);
    waitForExpectedElement(by).sendKeys(inputText2);
  }

  public void enterText(By by, String inputText) {
    waitForExpectedElement(by).sendKeys(inputText);
  }

  public void ScrolltoPageEnd() {
    Actions builder = new Actions(webDriver);
    // builder.keyDown(Keys.END).build().perform();
    builder.sendKeys(Keys.END).perform();
  }

  public void hitkeys(String key) {
    Actions builder = new Actions(webDriver);
    // builder.keyDown(Keys.END).build().perform();
    if (key.equals("tab")) {
      builder.sendKeys(Keys.TAB).perform();
    } else if (key.equals("enter")) {
      builder.sendKeys(Keys.RETURN).perform();
    } else if (key.equals("down")) {
      builder.sendKeys(Keys.ARROW_DOWN).perform();
    } else if (key.equals("up")) {
      builder.sendKeys(Keys.ARROW_UP).perform();
    } else if (key.equals("left")) {
      builder.sendKeys(Keys.ARROW_LEFT).perform();
    } else if (key.equals("right")) {
      builder.sendKeys(Keys.ARROW_RIGHT).perform();
    } else if (key.equals("PageDown")) {
      builder.sendKeys(Keys.PAGE_DOWN).perform();
    } else if (key.equals("b")) {
      builder.sendKeys(key).perform();
    }

  }

  /**
   * Wrapper for wait, clear data and sendKeys in Input Text box
   * <p>
   * * @param by Element location found by css, xpath, id etc...
   *
   * @param inputText text to be entered
   **/
  public void waitClearEnterText(By by, String inputText) {
    waitForExpectedElement(by).clear();
    waitForExpectedElement(by).sendKeys(inputText);
  }

  public void scrollDownWithJSExecutor() {
    ((JavascriptExecutor) webDriver).executeScript("scroll(0, 550);");
  }

  public void scrollDownWithJSExecutor1() {
    ((JavascriptExecutor) webDriver).executeScript("scroll(0, 850);");
  }

  public void scrollUpWithJSExecutor() {
    ((JavascriptExecutor) webDriver).executeScript("scroll(0, -550);");
  }

  public void clickwithXY(final By by) {
    WebElement element = webDriver.findElement(by);
    ((JavascriptExecutor) webDriver)
        .executeScript("window.scrollTo(" + element.getLocation().x + "," + element.getLocation().y + ")");
    // ((JavascriptExecutor)webDriver).executeScript("window.scrollTo(0,"+element.getLocation().x+")");
    element.click();
  }

  public void driverSwitch() {
    webDriver.switchTo();
  }

  public void browserRefresh() {
    webDriver.navigate().refresh();
  }

  public void selectDropDownText(By by, String text) {
    click(by);
    Select dropdown = new Select(webDriver.findElement(by));
    getFluentWait().until(ExpectedConditions.presenceOfNestedElementLocatedBy(by, By.tagName("option")));
    dropdown.selectByVisibleText(text);
  }

  public String getSelectedOption(By by) {
    return new Select(waitForExpectedElement(by)).getFirstSelectedOption().getText();
  }

  public String getValue(By by) {
    return waitForExpectedElement(by).getAttribute("value");
  }

  public List<String> getDropDownValues(By by) {
    String defaultSelectedValue = getSelectedOption(by);
    Select dropdown = new Select(webDriver.findElement(by));
    List<WebElement> webElementList = dropdown.getOptions();
    List<String> stringList = new ArrayList<>();
    for (WebElement value : webElementList) {
      if (!(value.getText().equals(defaultSelectedValue))) {
        stringList.add(value.getText());
        LOG.debug("stringList=" + stringList);
      }
    }
    return stringList;
  }

  public void waitForPageLoad() {
    ExpectedCondition<Boolean> pageLoadCondition = driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState")
        .equals("complete");
    getFluentWait().until(pageLoadCondition);
  }

  /**
   * implicit wait to test a few scenarios
   */
  public void implicitwait() {
    webDriver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
  }

  public void delayExecution(int seconds) {
    try {
      Thread.sleep(seconds * 1000L);
    } catch (InterruptedException e) {
      LOG.error(e);
    }
  }

  /**
   * generic method to switch to iframes with index
   */
  public WebDriver switchtoIframeWithIndex(final int index) {
    webDriver.switchTo().defaultContent();
    return webDriver.switchTo().frame(index);
  }

  public WebDriver switchtoDefault() {
    return webDriver.switchTo().defaultContent();
  }

  public void clickOnFirstValidLocator(String multipleLocatorsXpath) {
    //TODO
    throw new UnsupportedOperationException("Please add your own implementation");
  }

  public List<WebElement> getMultipleElements(By by) {
    getFluentWait().until(ExpectedConditions.visibilityOfElementLocated(by));
    return webDriver.findElements(by);
  }

  public int getNumberofElements(By by) {
    getFluentWait().until(ExpectedConditions.visibilityOfElementLocated(by));
    return webDriver.findElements(by).size();
  }

  public int getNumberOfElementsWithoutWait(By by) {
    return webDriver.findElements(by).size();
  }

  /*
   * Scroll till the element visible on the page
   */
  public void scrollIntoView_Element(By by) {
    JavascriptExecutor js = (JavascriptExecutor) webDriver;
    WebElement element = waitForExpectedElement(by);
    js.executeScript("arguments[0].scrollIntoView(true);", element);
  }

  public void scrollIntoView_Element(WebElement element) {
    JavascriptExecutor js = (JavascriptExecutor) webDriver;
    js.executeScript("arguments[0].scrollIntoView(true);", element);
  }

  /*
   * Peform "Esc" keypress event to close the popup/window
   */
  public void escapeWindow() {
    // Escape window (it will work as the escape button functionality in the keyboard
    Actions action = new Actions(webDriver);
    action.sendKeys(Keys.ESCAPE).build().perform();
  }

  public void switchToiFrame(final int iFrameIndex) {
    LOG.info("Switching to iFrame");
    try {
      // WebDriverWait wdw = new WebDriverWait(driver, timeOutInSeconds);
      webDriver.switchTo().defaultContent();
      getFluentWait().until(new ExpectedCondition<Boolean>() {
        public Boolean apply(WebDriver webDriver) {
          return webDriver.switchTo().frame(iFrameIndex) != null;
        }
      });
    } catch (Exception e) {
      LOG.error(e.getStackTrace());
      throw e;
    }
  }

  public void switchDirectlyToiFrame(final String iFrameName) {
    try {
      webDriver.switchTo().defaultContent();
      getFluentWait().until((ExpectedCondition<Boolean>) webDriver -> webDriver.switchTo().frame(iFrameName) != null);
    } catch (Exception e) {
      LOG.error(e.getStackTrace());
      throw e;
    }
  }

  public void ScrollToPageUp() {
    Actions builder = new Actions(webDriver);
    builder.sendKeys(Keys.PAGE_UP).perform();
  }

  // To get driver instance
  public WebDriver getDriver() {
    return webDriver;
  }

  public void Enter_KeyboardFunctionality() {
    // Page enter (it will work as the Enter button functionality in the keyboard
    Actions action = new Actions(webDriver);
    action.sendKeys(Keys.ENTER).build().perform();
  }

  public void openRecordIdPage(String recordId, String serverUrl) {
    String[] baseURL = serverUrl.split("/services");
    // Navigate to record Id page (e.g: oppt, Account, Lead or Contact page using
    // respective record id)
    String url = baseURL[0] + "/one/one.app#/sObject/" + recordId + "/view";
    webDriver.navigate().to(url);
  }

  // It won't wait default time when(60 sec) the element is not present
  public boolean isElementPresent_withOutTimeWait(final By by) {
    try {
      return webDriver.findElement(by).isDisplayed();
    } catch (Exception exception) {
      LOG.error(exception);
      return false;
    }
  }

  public void clearOnly(By by) {
    waitForExpectedElement(by).clear();
  }

  public void QuoteTable() {
    WebElement table = webDriver.findElement(By.xpath("//table/tbody"));
    List<WebElement> list = table.findElements(By.tagName("tr"));
    for (int i = 0; i < list.size(); i++) {
      List<WebElement> cols = table.findElements(By.tagName("td"));
      for (int j = 2; j < cols.size(); j++) {
        if (getText(cols.get(j)).equals("Void")) {
          j--;
          LOG.info(getText(cols.get(j)));
          LOG.info("Void found");
          cols.get(j).click();
          break;
        } else {
          LOG.info("No void records");
        }
      }
    }
  }

  public void NavigateToURL(String navigationURL) {
    webDriver.navigate().to(navigationURL);
  }

  public String ExecuteJs_toGetReferenceValue() {
    JavascriptExecutor js = (JavascriptExecutor) webDriver;
    return js.executeScript("return document.querySelector(Query)").toString();
  }

  @Deprecated
  public void jsClick(WebElement element) {
    LOG.warn(" *************** jsClick() is deprecated. Please use click() ******************");
    click(element);
  }

  @Deprecated
  public void jsClick(By by) {
    LOG.warn(" *************** jsClick() is deprecated. Please use click() ******************");
    click(by);
  }

  @Deprecated
  public void clickElementUsingJavascript(final By by) {
    LOG.warn(" *************** clickElementUsingJavascript() is deprecated. Please use click() ******************");
    jsClick(by);
  }

  @Deprecated
  public void clickOnElementWithJSExecutor(final By by) {
    LOG.warn(" *************** clickElementUsingJavascript() is deprecated. Please use click() ******************");
    jsClick(by);
  }

  @Deprecated
  public void clickOnElementWithJSExecutor(WebElement element) {
    LOG.warn(" *************** clickOnElementWithJSExecutor() is deprecated. Please use click() ******************");
    jsClick(element);
  }

  public void clickVisibleElement(By by) {
    List<WebElement> elements = webDriver.findElements(by);
    for (WebElement element : elements) {
      if (element.isDisplayed()) {
        element.click();
        break;
      }
    }
  }

  public void waitTillElementIsEnabled(By by) {
    ExpectedCondition<Boolean> elementEnabledCondition = new ExpectedCondition<Boolean>() {
      public Boolean apply(WebDriver driver) {
        return driver.findElement(by).isEnabled();
      }
    };
    getFluentWait().until(elementEnabledCondition);
  }

  //This can be used when there is no select tag
  public void selectDropDown(String visibleText, By by) {
    List<WebElement> listofElements = webDriver.findElements(by);
    for (WebElement itemInList : listofElements) {
      if (getText(itemInList).equalsIgnoreCase(visibleText)) {
        hoverAndClickOnElement(itemInList);
        break;
      }
    }
  }

  public void highlightElement(By by) {
    JavascriptExecutor jse = (JavascriptExecutor) webDriver;
    jse.executeScript("arguments[0].style.border='3px solid green'", webDriver.findElement(by));
  }

  public void waitForElementPolling(By by, long waitTime) {
    elementToBeClickable(by, waitTime);
  }
}