package com.dummycompany.test.lib.web.ui;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.dummycompany.test.framework.core.utils.Props;
import com.dummycompany.test.lib.api.RequestSpecificationFactory;
import io.cucumber.java.sl.In;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.*;
import java.util.NoSuchElementException;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.*;
import org.openqa.selenium.internal.*;
import org.openqa.selenium.support.events.EventFiringWebDriver;

public class SmartRemoteWebDriver implements WebDriver, JavascriptExecutor, FindsById, FindsByClassName, FindsByLinkText, FindsByName, FindsByCssSelector, FindsByTagName, FindsByXPath, HasInputDevices, HasCapabilities, Interactive, TakesScreenshot, SearchContext {

    private static final Logger log = LogManager.getLogger(DriverHelper.class);
    public EventFiringWebDriver remoteDriver;
    public boolean smartDetection, updateHistory;
    private double matchingThreshold=0;
    private final String storeWebElementInfoUrl="https://744bjsvaw1.execute-api.ap-southeast-2.amazonaws.com/dev/build-corpus";
    private final String smartElementSearchUrl ="https://x4v7gu6df2.execute-api.ap-southeast-2.amazonaws.com/v1/ai";
    private final String reportUsageUrl="https://mbqzsx7u50.execute-api.ap-southeast-2.amazonaws.com/dev/post-reporting-data";
    private String teamName;
    PropertiesConfiguration exclusionList;

    public SmartRemoteWebDriver(EventFiringWebDriver driver) {
        remoteDriver = driver;
        smartDetection = Boolean.parseBoolean(Props.getProp("smart_web_element_detection_enabled"));
        if (smartDetection) {
            updateHistory = Boolean.parseBoolean((Props.getProp("update_history")));
            matchingThreshold = Double.parseDouble(Props.getProp("matching_threshold"))/100;
            exclusionList = getExclusionList();
            teamName = Props.getProp("team_name");
        }
    }

    @Override
    public Navigation navigate() {
        return remoteDriver.navigate();
    }

    @Override
    public Object executeAsyncScript(String arg0, Object... arg1) {
        return this.remoteDriver.executeAsyncScript(arg0, arg1);
    }

    @Override
    public Object executeScript(String arg0, Object... arg1) {
        return this.remoteDriver.executeScript(arg0, arg1);
    }

    @Override
    public WebElement findElementByXPath(String arg0) {
        return this.findElement(By.xpath(arg0));
    }

    @Override
    public List<WebElement> findElementsByXPath(String arg0) {
        return this.remoteDriver.findElements(By.xpath(arg0));
    }

    @Override
    public WebElement findElementByTagName(String arg0) {
        return this.findElement(By.tagName(arg0));
    }

    @Override
    public List<WebElement> findElementsByTagName(String arg0) {
        return this.remoteDriver.findElements(By.tagName(arg0));
    }

    @Override
    public WebElement findElementByName(String arg0) {
        return this.findElement(By.name(arg0));
    }

    @Override
    public List<WebElement> findElementsByName(String arg0) {
        return this.remoteDriver.findElements(By.name(arg0));
    }

    @Override
    public WebElement findElementByLinkText(String arg0) {
        return this.findElement(By.linkText(arg0));
    }

    @Override
    public List<WebElement> findElementsByLinkText(String using) {
        return this.findElements(By.linkText(using));
    }

    @Override
    public WebElement findElementByPartialLinkText(String arg0) {
        return this.findElement(By.partialLinkText(arg0));
    }

    @Override
    public List<WebElement> findElementsByPartialLinkText(String arg0) {
        return this.remoteDriver.findElements(By.partialLinkText(arg0));
    }

    @Override
    public WebElement findElementById(String arg0) {
        return this.findElement(By.id(arg0));
    }


    @Override
    public List<WebElement> findElementsById(String arg0) {
        return this.remoteDriver.findElements(By.id(arg0));
    }

    @Override
    public WebElement findElementByCssSelector(String arg0) {
        return this.findElement(By.cssSelector(arg0));
    }

    @Override
    public List<WebElement> findElementsByCssSelector(String arg0) {
        return this.remoteDriver.findElements(By.cssSelector(arg0));
    }

    @Override
    public WebElement findElementByClassName(String arg0) {
        return this.findElement(By.className(arg0));
    }

    @Override
    public List<WebElement> findElementsByClassName(String arg0) {
        return this.remoteDriver.findElements(By.className(arg0));
    }

    @Override
    public void perform(Collection<Sequence> arg0) {
        this.remoteDriver.perform(arg0);
    }

    @Override
    public void resetInputState() {
        this.remoteDriver.resetInputState();
    }

    @Override
    public Keyboard getKeyboard() {
        return this.remoteDriver.getKeyboard();
    }

    @Override
    public Mouse getMouse() {
        return this.remoteDriver.getMouse();
    }

    @Override
    public Capabilities getCapabilities() {
        return this.remoteDriver.getCapabilities();
    }

    @Override
    public void close() {
        this.remoteDriver.close();
    }

    @Override
    public void get(String arg0) {
        this.remoteDriver.get(arg0);
    }

    @Override
    public String getCurrentUrl() {
        return this.remoteDriver.getCurrentUrl();
    }

    @Override
    public String getPageSource() {
        return this.remoteDriver.getPageSource();
    }

    @Override
    public String getTitle() {
        return this.remoteDriver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return this.remoteDriver.findElements(by);
    }

    @Override
    public String getWindowHandle() {
        return this.remoteDriver.getWindowHandle();
    }

    @Override
    public Set<String> getWindowHandles() {
        return this.remoteDriver.getWindowHandles();
    }

    @Override
    public Options manage() {
        return this.remoteDriver.manage();
    }

    @Override
    public void quit() {
        this.remoteDriver.quit();
    }

    @Override
    public TargetLocator switchTo() {
        return this.remoteDriver.switchTo();
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> arg0) throws WebDriverException {
        return this.remoteDriver.getScreenshotAs(arg0);
    }

    @Override
    public WebElement findElement(By by) {
        if (!smartDetection || isLocatorInExclusionList(by)) {
            return remoteDriver.findElement(by);
        } else {
            NoSuchElementException noSuchElementException = null;
            WebElement webElement = null;
            try{
                webElement = remoteDriver.findElement(by);
            }catch (NoSuchElementException nse){
                noSuchElementException = nse;
                log.info("Element couldn't be located by Selenium");
            }
            if(webElement==null){
                // call the api to find a potential match
                int firstIndex = by.toString().indexOf(':');
                String strategyKey = by.toString().substring(0, firstIndex);
                String strategyValue = by.toString().substring(firstIndex+1);
                String[] strategyValues = strategyValue.split("//");
                String xpath = callSmartElementLocator(by, remoteDriver.findElement(By.xpath("html")).getAttribute("outerHTML"));
                if(xpath!=null)
                {//Successfully found the element
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("###############################################")
                        .append("\n")
                        .append("Smart Element Identification has identified a change in element locator strategy!")
                        .append("\n")
                        .append("Please use the below information and update your code!")
                        .append("\n")
                        .append("Full Url: ").append(remoteDriver.getCurrentUrl())
                        .append("\n")
                        .append("Locator Strategy: ").append(by.toString().split(":")[0].trim())
                        .append("\n")
                        .append("Strategy Value: ").append(by.toString().split(":")[1].trim())
                        .append("\n")
                        .append("New Xpath: ").append(xpath)
                        .append("\n")
                        .append("###############################################");
                    log.info(StringEscapeUtils.escapeJava(stringBuilder.toString()));
                    reportUsage(true, xpath);
                    return remoteDriver.findElement(By.xpath(xpath));
                }
                else {
                    if ((strategyKey.contains("xpath") && strategyValues.length > 2) &&
                            !(strategyValue.contains("ancestor") || strategyValue.contains("ancestor-or-self") ||
                                    strategyValue.contains("following::") || strategyValue.contains("preceding::")))
                    {
                        xpath = "";
                        boolean firstIteration=true;
                        for (String splitXpath : strategyValues) {
                            if (!splitXpath.trim().equals("")) {
                                WebElement element=null;
                                try {
                                    element = this.remoteDriver.findElement(By.xpath(xpath + "//" + splitXpath));
                                    xpath = xpath + "//" + splitXpath;
                                }catch (NoSuchElementException nse) {
                                    reportUsage(false, xpath);
                                    throw noSuchElementException;
                                }
                                if(element==null) {
                                    String tmpXpath=null;
                                    if(splitXpath.contains("parent")){
                                        tmpXpath = xpath + "//parent::*";
                                    }else if(splitXpath.contains("child") || splitXpath.contains("descendant::")){
                                        tmpXpath = xpath + "//child::*";
                                    }else if(splitXpath.contains("following-sibling")){
                                        tmpXpath = xpath + "//following-sibling::*";
                                    }else if(splitXpath.contains("preceding-sibling")){
                                        tmpXpath = xpath + "//preceding-sibling::*";
                                    }else{
                                        tmpXpath = xpath;
                                    }
                                    String dom="";
                                    if(splitXpath.contains("following-sibling") || splitXpath.contains("preceding-sibling")){
                                        try {
                                            List<WebElement> elements = this.remoteDriver.findElements(By.xpath(tmpXpath));
                                            for (WebElement tmpElement:elements) {
                                                dom = dom + tmpElement.getAttribute("outerHTML");
                                            }
                                        }catch (NoSuchElementException nse) {
                                            reportUsage(false, xpath);
                                            throw noSuchElementException;
                                        }
                                    }else{
                                        try {
                                            if(firstIteration) {
                                                dom = remoteDriver.findElement(By.xpath("html")).getAttribute("outerHTML");
                                                firstIteration=false;
                                            }
                                            else {
                                                WebElement tmpElement = this.remoteDriver.findElement(By.xpath(tmpXpath));
                                                dom = dom + tmpElement.getAttribute("outerHTML");
                                            }
                                        }catch (NoSuchElementException nse) {
                                            reportUsage(false, xpath);
                                            throw noSuchElementException;
                                        }
                                    }
                                    String newXpath = callSmartElementLocator(By.xpath(xpath + "//" + splitXpath), dom);
                                    if(newXpath==null){
                                        reportUsage(false, xpath);
                                        throw noSuchElementException;
                                    }
                                    xpath = xpath + newXpath;
                                }
                            }
                        }
                        //Successfully found the element
                      StringBuilder stringBuilder = new StringBuilder();
                      stringBuilder.append("###############################################")
                          .append("\n")
                          .append("Smart Element Identification has identified a change in element locator strategy!")
                          .append("\n")
                          .append("Please use the below information and update your code!")
                          .append("\n")
                          .append("Full Url: ").append(remoteDriver.getCurrentUrl())
                          .append("\n")
                          .append("Locator Strategy: ").append(by.toString().split(":")[0].trim())
                          .append("\n")
                          .append("Strategy Value: ").append(by.toString().split(":")[1].trim())
                          .append("\n")
                          .append("New Xpath: ").append(xpath)
                          .append("\n")
                          .append("###############################################");
                      log.info(stringBuilder.toString());
                        reportUsage(true, xpath);
                        return remoteDriver.findElement(By.xpath(xpath));
                    } else {
                        throw noSuchElementException;
                    }
                }
            }else{
                if (updateHistory) {
                    // call api to send information about the element
                    int firstIndex = by.toString().indexOf(':');
                    String strategyKey = by.toString().substring(0, firstIndex);
                    String strategyValue = by.toString().substring(firstIndex+1);
                    String[] strategyValues = strategyValue.split("//");
                    if (strategyKey.contains("xpath") || strategyValues.length>2)
                    {
                        StringBuilder xpath = new StringBuilder();
                        for (String splitXpath: strategyValues ) {
                            if (!splitXpath.trim().equals("")) {
                                xpath.append("//").append(splitXpath);
                                WebElement element = this.remoteDriver.findElement(By.xpath(xpath.toString()));
                                storeWebElementInfo(element, By.xpath(xpath.toString()));
                            }
                        }
                    }
                    else {
                        storeWebElementInfo(webElement, by);
                    }
                }
                return webElement;
            }
        }
    }

    private void storeWebElementInfo(WebElement webElement, By by){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("uuid", UUID.randomUUID().toString());
        jsonObject.put("timestamp", Instant.now());
        jsonObject.put("fullUrl", getCurrentUrlWithoutDynamicBits());
        jsonObject.put("method", "findElement");
        jsonObject.put("strategyKey", by.toString().split(":")[0].trim());
        jsonObject.put("strategyValue", by.toString().split(":")[1].trim());
        jsonObject.put("xpath", getAbsoluteXPath(webElement));
        jsonObject.put("cssSelector", getCssSelector(webElement));
        jsonObject.put("tag", webElement.getAttribute("outerHTML"));
        jsonObject.put("teamName", teamName);
        log.info("Storing Element Info : " + jsonObject.toString());
        RequestSpecification request = RequestSpecificationFactory.getInstanceWithoutLogs(null).accept(ContentType.JSON).contentType(ContentType.JSON).header("InvocationType", "Event")
                .body(jsonObject.toString());
        log.info("Request: " + request.log().all(true));
        Response response = request.post(storeWebElementInfoUrl);
        log.info("Response: " + response.then().log().all(true));
        Assert.assertNotNull("Storing web element information to build corpus has failed to respond", response);
        Assert.assertEquals("Storing web element information to build corpus has failed", 200, response.getStatusCode());
        log.info("Storing Element Info Successful! ");
    }

    private String callSmartElementLocator(By by, String dom) {
        String xpath=null;
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fullUrl", getCurrentUrlWithoutDynamicBits());
        jsonObject.put("method", "findElement");
        jsonObject.put("strategyKey", by.toString().split(":")[0].trim());
        jsonObject.put("strategyValue", by.toString().split(":")[1].trim());
        jsonObject.put("dom", dom);
        jsonObject.put("teamName", teamName);
        jsonObject.put("threshold", matchingThreshold);
        RequestSpecification request = RequestSpecificationFactory.getInstanceWithoutLogs(null).accept(ContentType.JSON).contentType(ContentType.JSON)
                .body(jsonObject.toString());
        log.info("Request Body: " + jsonObject.toString());
        Response response = request.post(smartElementSearchUrl);
        log.info("Response: " + response.getBody().asString());
        Assert.assertNotNull("Smart Element Locator failed to respond.", response);
        JSONObject responseJsonObj=null;
        try {
            responseJsonObj = new JSONObject(response.getBody().asString());
            int code = responseJsonObj.getJSONObject("Message").getInt("StatusCode");
            if (code != 200)
                return null;
        }catch (Exception e){
            return null;
        }
        ArrayList<HashMap<String,String>> matchingList;
        Gson gson = new Gson();
        Type listType = new TypeToken<List<HashMap<String, String>>>(){}.getType();
        matchingList = gson.fromJson( responseJsonObj.getJSONObject("Message").getString("response") , listType);
        if(matchingList.size()==1)
            return matchingList.get(0).get("xpath");
        else{
            float firstScore = Float.parseFloat(matchingList.get(0).get("ratio"));
            float secondScore = Float.parseFloat(matchingList.get(1).get("ratio"));
            float difference = firstScore - secondScore;
            if((firstScore==1.0 && secondScore<1.0) || difference>0.01)
                return matchingList.get(0).get("xpath");
            else
                return null;
        }
    }

    private void reportUsage(boolean matchFound, String xpath){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("increamentTotalCount", true);
        jsonObject.put("increamentSuccessCount", matchFound);
        jsonObject.put("xpath", xpath);
        jsonObject.put("class", "NA");
        Response response = RequestSpecificationFactory.getInstanceWithoutLogs(null).accept(ContentType.JSON).contentType(ContentType.JSON).header("InvocationType", "Event")
                .body(jsonObject.toString()).when().post(reportUsageUrl);
        Assert.assertNotNull("Reporting usage information has failed to respond", response);
        Assert.assertEquals("Reporting usage information has failed", 200, response.getStatusCode());
    }

    private String getAbsoluteXPath(WebElement webElement)
    {
        String xpath= "";
        try {
            xpath = (String) remoteDriver.executeScript(
                    "function absolutePath(element) {\n" +
                            "console.log(element);\n" +
                            "if (element.tagName.toLowerCase() == 'html')\n" +
                            "    return '/html[1]';\n" +
                            "if (element === document.body)\n" +
                            "    return '/html[1]/body[1]';\n" +
                            "var ix = 0;\n" +
                            "var siblings = element.parentNode.childNodes;\n" +
                            "for (var i = 0; i < siblings.length; i++) {\n" +
                            "    var sibling = siblings[i];\n" +
                            "    if (sibling === element)\n" +
                            "        return absolutePath(element.parentNode) + '/' + element.tagName.toLowerCase() + '[' + (ix + 1) + ']';\n" +
                            "    if (sibling.nodeType === 1 && sibling.tagName.toLowerCase() === element.tagName.toLowerCase())\n" +
                            "        ix++;\n" +
                            "}} return absolutePath(arguments[0]);", webElement);
        }catch (JavascriptException jse){
            log.error(jse);
        }
        return xpath;
    }

    private String getCssSelector(WebElement webElement){
        String selector = "";
        final String JS_BUILD_CSS_SELECTOR =
                "for(var e=arguments[0],n=[],i=function(e,n){if(!e||!n)return 0;f" +
                        "or(var i=0,a=e.length;a>i;i++)if(-1==n.indexOf(e[i]))return 0;re" +
                        "turn 1};e&&1==e.nodeType&&'HTML'!=e.nodeName;e=e.parentNode){if(" +
                        "e.id){n.unshift('#'+e.id);break}for(var a=1,r=1,o=e.localName,l=" +
                        "e.className&&e.className.trim().split(/[\\s,]+/g),t=e.previousSi" +
                        "bling;t;t=t.previousSibling)10!=t.nodeType&&t.nodeName==e.nodeNa" +
                        "me&&(i(l,t.className)&&(l=null),r=0,++a);for(var t=e.nextSibling" +
                        ";t;t=t.nextSibling)t.nodeName==e.nodeName&&(i(l,t.className)&&(l" +
                        "=null),r=0);n.unshift(r?o:o+(l?'.'+l.join('.'):':nth-child('+a+'" +
                        ")'))}return n.join(' > ');";

        JavascriptExecutor js = remoteDriver;
        try {
            selector = (String) js.executeScript(JS_BUILD_CSS_SELECTOR, webElement);
        }catch (JavascriptException jse){
            log.error(jse);
        }
        return  selector;
    }

    private String getCurrentUrlWithoutDynamicBits(){
        String fullUrl = remoteDriver.getCurrentUrl();
        if(fullUrl.contains("?"))
            fullUrl = fullUrl.substring(0,fullUrl.lastIndexOf('?'));
        int lastIndex = fullUrl.lastIndexOf('/');
        if(fullUrl.charAt(lastIndex-1)!='/' && lastIndex<fullUrl.length()-1)
            if(fullUrl.substring(lastIndex).contains("#"))
                fullUrl = fullUrl.substring(0, fullUrl.substring(lastIndex).indexOf("#")+lastIndex);
        return fullUrl;
    }

    private PropertiesConfiguration getExclusionList(){
        PropertiesConfiguration properties = new PropertiesConfiguration();
            try{
                InputStream inputStream = Props.class.getResourceAsStream("/exclusions.properties");
                if(inputStream!=null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    properties.read(inputStreamReader);
                    inputStreamReader.close();
                    log.info(properties);
                }
                Objects.requireNonNull(inputStream).close();
            } catch (IOException | ConfigurationException e) {
                log.error(e.getMessage());
            }
        return properties;
    }

    private boolean isLocatorInExclusionList(By by){
        int firstIndex = by.toString().indexOf(':');
        String strategyKey = by.toString().substring(0, firstIndex);
        String strategyValue = by.toString().substring(firstIndex+1);
        return Arrays.asList(exclusionList.getStringArray(strategyKey)).contains(strategyValue);
    }

}
