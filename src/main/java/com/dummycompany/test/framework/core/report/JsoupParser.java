package com.dummycompany.test.framework.core.report;

import com.dummycompany.test.framework.core.db.DBConnectionUtils;
import org.json.CDL;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The JsoupParser class extracts the following data from the cucumber HTML
 * Repory - O2A stage status (pass\fail) details are captured and passed it to
 * splunk custom json for reporting - Evendence URL is captured and passed to
 * splunk cusom json for reporting
 */

public class JsoupParser {

    static org.json.JSONArray stepsInfo;
    static String o2aSteps = "stepName,fullName,stepData,status,stepId \n";
    static String stepActivity = "";
    static String stepStatus = "";
    static String eventId = "";
    static String stepFullName = "";
    static String orderId = "";
    static String stepsData = "";
    static String orderID = "OrderID:";
    static String eventID = "EventID:";
    static JSONObject o2aData = new JSONObject();
    private static int pTagStepID;
    private static int stepID = 201;
    private static final Logger LOG = LogManager.getLogger(DBConnectionUtils.class);

    @SuppressWarnings("unchecked")
    public static void getO2AStepDataFromHtmlReport(org.json.simple.JSONArray scData) throws JSONException {
        try {
            String overviewFeatureHtml = "target/cucumber-html-reports/overview-features.html";
            String processFtrHtml = null;
            File inputFeatureHTML = new File(overviewFeatureHtml);
            Document doc = Jsoup.parse(inputFeatureHTML, "UTF-8");
            Elements featureTagname = doc.select("td.tagname");
            for (Element ftrElement : featureTagname) {
                processFtrHtml = ftrElement.select("a").attr("href");
                File reportFile = new File("target/cucumber-html-reports/" + processFtrHtml);
                Document reportFileDoc = Jsoup.parse(reportFile, "UTF-8");
                Elements preTagFail = reportFileDoc.getElementsByTag("pre");
                Elements pTagPass = reportFileDoc.select("p");
                String preTagList = "";
                String pTagList = "";
                // O2A Failed & Skipped Steps
                for (Element eachElem : preTagFail) {
                    if (eachElem.text()
                            .contains("java.lang.AssertionError: Capturing EOMSYS activity and events for order id")) {
                        preTagList = eachElem.text();
                    }
                }
                getO2AStepDataInfo(scData, stepID, preTagList);
                // O2A Passed Skipped Steps
                for (Element eachElem : pTagPass) {
                    pTagList = pTagList + eachElem.text() + "\n";

                }
                getO2AStepDataInfo(scData, pTagStepID, pTagList);

                o2aData.put("application", "EOMSYS");
                o2aData.put("scenario", "EOMSYS Order Activities");
                o2aData.put("type", "API");
                stepsInfo = CDL.toJSONArray(o2aSteps);
                if (stepsInfo != null) {
                    o2aData.put("steps", stepsInfo);
                    LOG.info("--- In JsoupParser - Extracted O2A steps ---  :" + o2aData);
                    scData.add(o2aData);
                }
            }
        } catch (IOException ie) {
            LOG.error("-- Error in extracting the O2A step data from HTML report -- " + ie);
        }
    }

    public static void getO2AStepDataInfo(org.json.simple.JSONArray scData, int stepID, String tagList) {
        String stepLines[] = tagList.split("\n");
        for (int i = 0; i < stepLines.length; i++) {
            if (stepLines[i].contains("Capturing EOMSYS activity and events for order id ")) {
                orderId = (stepLines[i].substring(stepLines[i].lastIndexOf(' '))).toString().trim();
            } else if ((stepLines[i] != null) && (!(stepLines[i].isEmpty())) && (stepLines[i].contains("Activity:"))) {
                String stepDetails[] = stepLines[i].split("\\|");
                stepActivity = stepDetails[0].substring(stepDetails[0].indexOf(":") + 1).trim();
                eventId = stepDetails[1].substring(stepDetails[1].indexOf(":") + 1).trim();
                stepStatus = stepDetails[2].substring(stepDetails[2].indexOf(":") + 1).trim();
                stepFullName = (stepID + " " + stepActivity).toString().trim();
                stepsData = (orderID + orderId + "|" + eventID + eventId).toString().trim();
                o2aSteps = o2aSteps + stepActivity + "," + stepFullName + "," + stepsData + "," + stepStatus + ","
                        + stepID + "\n";
                stepID++;
            }
        }
        pTagStepID = stepID;
    }

    @SuppressWarnings("unchecked")
    public static String getEvidenceUrlForApplication(String fName, String davApp) throws JSONException {
        String evidenceURLs = "";
        try {
            String overviewFeatureHtml = "target/cucumber-html-reports/overview-features.html";
            String processfeatureFile = null;
            String applicationName = null;
            File inputFeatureHtml = new File(overviewFeatureHtml);
            Document featureDoc = Jsoup.parse(inputFeatureHtml, "UTF-8");
            Elements featureTagName = featureDoc.select("td.tagname");
            for (Element featureElement : featureTagName) {
                processfeatureFile = featureElement.select("a").attr("href");
                File featureReportFile = new File("target/cucumber-html-reports/" + processfeatureFile);
                Document featureFileDoc = Jsoup.parse(featureReportFile, "UTF-8");
                String featureNameTitle = featureFileDoc.title();
                String featureName = featureNameTitle.substring(featureNameTitle.lastIndexOf(":") + 1).toString().trim()
                        .replace(" ", "").replace("&amp;nbsp;", "").replace("&nbsp;", "")
                        .replaceAll("[^\\p{ASCII}]", "").trim();
                String businessScenario = fName.replace(" ", "").replace(" ", "").replaceAll("[^\\p{ASCII}]", "")
                        .trim();
                if (businessScenario.equalsIgnoreCase(featureName)) {
                    Elements imageEmbedContent = featureFileDoc.select("div.steps.inner-level")
                            .select("div.embedding-content");
                    ArrayList<String> failedImageList = new ArrayList<String>();
                    for (Element imageElement : imageEmbedContent) {
                        String appInHTML = Objects.requireNonNull(Objects.requireNonNull(imageElement.parent().parent().parent().parent().parent().parent().parent()
                                .previousElementSibling()).previousElementSibling()).toString();
                        if (appInHTML.contains("Vlocity")) {
                            applicationName = "Vlocity";
                        } else if (appInHTML.contains("AEM")) {
                            applicationName = "AEM";
                        } else if (appInHTML.contains("AgentConsole")) {
                            applicationName = "AgentConsole";
                        } else if (appInHTML.contains("Mydummycompany")) {
                            applicationName = "Mydummycompany";
                        } else if (appInHTML.contains("EOV")) {
                            applicationName = "EOV";
                        } else if (appInHTML.contains("O2A_U2C_")) {
                            applicationName = "O2A_U2C";
                        }
                        if (appInHTML.contains("failed") && davApp.equalsIgnoreCase(applicationName)) {
                            failedImageList.add(System.getenv("BUILD_URL") + "/cucumber-html-reports/"
                                    + imageElement.select("img").attr("src"));
                            int length = failedImageList.size();
                            evidenceURLs = failedImageList.get(length - 1);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("-- Error in extracting the Evidence URL from HTML report -- " + e);
        }
        return evidenceURLs;
    }

    @SuppressWarnings("unchecked")
    public static String getVelocityOrderId(String featureName) throws JSONException {
        String velocityOrderID = null;
        try {
            String ovrFeatureHtml = "target/cucumber-html-reports/overview-features.html";
            String velocityProcessFtrHtml;
            File inputFeatureHTML = new File(ovrFeatureHtml);
            Document doc = Jsoup.parse(inputFeatureHTML, "UTF-8");
            Elements featureTagname = doc.select("td.tagname");
            for (Element ftrElement : featureTagname) {
                velocityProcessFtrHtml = ftrElement.select("a").attr("href");
                File velocityReportFile = new File("target/cucumber-html-reports/" + velocityProcessFtrHtml);
                Document velocityReportFileDoc = Jsoup.parse(velocityReportFile, "UTF-8");
                String fnTitle = velocityReportFileDoc.title();
                String fnName = fnTitle.substring(fnTitle.lastIndexOf(":") + 1).trim().replace(" ", "")
                        .replace("&amp;nbsp;", "").replace("&nbsp;", "").replaceAll("[^\\p{ASCII}]", "").trim();
                String businessScenario = featureName.replace(" ", "").replace(" ", "").replaceAll("[^\\p{ASCII}]", "")
                        .trim();
                if (businessScenario.equalsIgnoreCase(fnName)) {
                    Elements velocityElements = velocityReportFileDoc.select("div.element").select("div.brief.passed");
                    for (Element elm : velocityElements) {
                        if (elm.text().contains("Scenario Vlocity_")) {
                            Elements velocityPreTags = elm.parent().parent().select("div.outputs.inner-level")
                                    .select("p");
                            for (Element el : velocityPreTags) {
                                if (el.text().contains("* Order id:")) {
                                    velocityOrderID = "VelocityOrderId:"
                                            + el.text().substring(el.text().lastIndexOf(":") + 1).trim();
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException ie) {
            LOG.error("-- Error in extracting the velocity orderid data from HTML report -- " + ie);
        }
        return velocityOrderID;
    }

    public static void main(String[] args) throws JSONException {
        // getVelocityOrderId();
        // static JSONArray scenarioData = new JSONArray();
        // getEvidenceURLFromHtmlReport();
        // getO2AStepDataFromHtmlReport(scData);
    }
}