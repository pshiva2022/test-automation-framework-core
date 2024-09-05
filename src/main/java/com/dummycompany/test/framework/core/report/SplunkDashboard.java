package com.dummycompany.test.framework.core.report;

import com.dummycompany.test.framework.core.api.ApiHandler;
import com.dummycompany.test.framework.core.db.DBConnectionUtils;
import com.dummycompany.test.framework.core.utils.Props;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.json.CDL;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Objects;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SplunkDashboard {

    static String featureName = "";
    static int scenarioCount = 0;
    static String triggeredAt = "";
    static JSONArray scenarioData = new JSONArray();
    static JSONObject createdJsonData = null;
    static String NewJsonData = "";
    static String scenarioName = "";
    static String stepErrMsg = "";
    static String stepDateString = "";
    static long stepDurationInMs = 0;
    static String stepsTimeStamp = "";
    static Date stepDate;
    static String stepsDataInfo = "";
    static String vlocityOrderId = "";
    static String env = "";
    static String jiraDefect = "";
    static String evidenceURL = "";
    private static final Logger LOG = LogManager.getLogger(SplunkDashboard.class);

    public static void main(String[] args) throws JSONException {
        Props.setSystemProp("tags", "@dashboard");
        prepareAndSendDataToSplunk();

    }

    public static void prepareAndSendDataToSplunk() {
        try {
            if (Props.getEnvOrPropertyValue("tags") != null
                    && Props.getEnvOrPropertyValue("tags").toLowerCase().contains("dashboard")) {
                LOG.info("* Checking available data to be send to splunk dashboard");
                File folder = new File("./target/cucumber-parallel/json/");
                File[] listOfFiles = folder.listFiles();
                for (int fn = 0; fn < Objects.requireNonNull(listOfFiles).length; fn++) {
                    if (listOfFiles[fn].isFile()) {
                        scenarioData.clear();
                        triggeredAt = "";
                        env = Props.getEnvOrPropertyValue("env");
                        if (StringUtils.isNotBlank(env)) {
                            env = "NA";
                        }
                        parseJsonData(listOfFiles[fn].getName());// read data from json files
                        JsoupParser.getO2AStepDataFromHtmlReport(scenarioData);
                        createdJsonData = addFeatureData(env, featureName);
                        NewJsonData = createdJsonData.toString();
                        LOG.info("* Preparing data for file " + listOfFiles[fn].getName());
                        String data = readXmlData(listOfFiles[fn].getName());
                        LOG.info(data);
                        sendDataToSvtSplunk(data.toString(), listOfFiles[fn].getName());
                        sendDataToNonProdBambiSplunk(data.toString(), listOfFiles[fn].getName());
                        sendDataToProdBambiSplunk(data.toString(), listOfFiles[fn].getName());
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("* Error while reading/sending data to dashboard");
        }
    }

    public static String readJsonData(String filename) {
        String tempData = "";
        String data = "";
        try {
            File file = new File("./target/cucumber-parallel/json/" + filename);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null) {
                tempData = tempData + st;
            }
            data = tempData.substring(1, tempData.length() - 1);
            br.close();
            return data;
        } catch (Exception e) {
            LOG.error("Error reading json data from a file");
        }
        return data;
    }

    @SuppressWarnings("unchecked")
    public static void parseJsonData(String filename) throws Exception {
        String[] sceData = new String[4];
        int stepID = 111;
        String status;
        org.json.JSONArray stepsdata;
        String data = readJsonData(filename);
        JSONParser parser = new JSONParser();
        JSONObject obj = (JSONObject) parser.parse(data);
        featureName = (String) obj.get("name");
        // ---------scenario details below--------------
        JSONArray eleData = (JSONArray) obj.get("elements");
        Iterator ele_itr = eleData.iterator();
        while (ele_itr.hasNext()) {
            String steps = "stepId, stepName, fullName, status, stepduration, steptime, stepdata, jiradefectid \n";
            stepsdata = null;
            scenarioCount = scenarioCount + 1;
            Object slide = ele_itr.next();
            JSONObject jsonObject2 = (JSONObject) slide;
            String scenarioName = (String) jsonObject2.get("name");
            String[] test = scenarioName.split("_");
            // sceData[0] = test[0];
            // sceData[1] = test[1];
            if (scenarioName.startsWith("O2A_U2C_")) {
                sceData[0] = "O2A_U2C";
                sceData[1] = scenarioName.substring(scenarioName.lastIndexOf("_") + 1);
            } else {
                sceData[0] = test[0];
                sceData[1] = test[1];
            }
            if (test[1].toLowerCase().contains("api")) {
                sceData[2] = "API";
            } else {
                sceData[2] = "UI";
            }
            JSONArray sceAfterData = (JSONArray) jsonObject2.get("after");
            Iterator sceAfter_itr = sceAfterData.iterator();
            while (sceAfter_itr.hasNext()) {
                Object slide1 = sceAfter_itr.next();
                JSONObject jsonObject23 = (JSONObject) slide1;
                JSONObject scenariorResult = (JSONObject) jsonObject23.get("result");
                status = (String) scenariorResult.get("status");
                sceData[3] = status;
            }
            // ---------steps details below--------------
            // fetching step data result
            JSONArray stepsData = (JSONArray) jsonObject2.get("steps");
            Iterator stepData_itr = stepsData.iterator();
            while (stepData_itr.hasNext()) {
                Object slide2 = stepData_itr.next();
                JSONObject jsonObject24 = (JSONObject) slide2;
                String stepKeyword = (String) jsonObject24.get("keyword");
                String temp_stepName = (String) jsonObject24.get("name");
                temp_stepName = temp_stepName.replace(",", ".");
                String stepName = stepKeyword + temp_stepName;
                JSONObject stepResult = (JSONObject) jsonObject24.get("result");
                status = (String) stepResult.get("status");
                Object stepDuration = stepResult.get("duration");
                scenarioName = (String) jsonObject2.get("name");
                String timeStamp = getStartedAtFromXML(scenarioName, filename);
                String stepTimeStamp = getStepTimeStamp(timeStamp, stepDuration, status);
                if (status == "failed" || status.equalsIgnoreCase("failed") || status == "passed"
                        || status.equalsIgnoreCase("passed")) {
                    stepDateString = stepDateString;
                } else {
                    stepDateString = null;
                }
                if (sceData[0].equalsIgnoreCase("Vlocity")) {
                    vlocityOrderId = JsoupParser.getVelocityOrderId(featureName);
                    stepsDataInfo = vlocityOrderId;
                } else {
                    stepsDataInfo = null;
                }
                if (status == "failed" || status.equalsIgnoreCase("failed")) {
                    evidenceURL = JsoupParser.getEvidenceUrlForApplication(featureName, sceData[0]);
                    //jiraDefect = JIRAIntegration.createJiraIntegrationDefect(sceData[0], stepTimeStamp, stepName, env, featureName,
                    //evidenceURL, stepsDataInfo);
                } else {
                    jiraDefect = null;
                }
                steps = steps + stepID + "," + stepName + "," + stepID + " " + stepName + "," + status + ","
                        + stepDateString + "," + stepTimeStamp + "," + stepsDataInfo + "," + jiraDefect + "\n";
                stepID++;
            }
            stepsdata = CDL.toJSONArray(steps.replace("\"", ""));
            addScenarioData(sceData, stepsdata, evidenceURL);
            steps = null;
        }
    }

    private static String readXmlData(String filename) {
        String tempData = "";
        try {
            File fXmlFile = new File("./target/cucumber-parallel/testng/" + filename.replace(".json", ".xml"));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("test-method");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (Objects.requireNonNull(nNode).getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String sceName = eElement.getAttribute("name");
                    for (int i = 0; i < scenarioCount; i++) {
                        String tempSceName = ApiHandler.getJsonTagValue("event.scenarioData[" + i + "].scenario",
                                createdJsonData.toString());
                        if (sceName.contains(tempSceName)) {
                            // System.out.println("Started at"+eElement.getAttribute("started-at"));
                            tempData = ApiHandler.setJsonTagValue("event.scenarioData[" + i + "].started-at",
                                    NewJsonData, eElement.getAttribute("started-at"));
                            tempData = ApiHandler.setJsonTagValue("event.scenarioData[" + i + "].finished-at", tempData,
                                    eElement.getAttribute("finished-at"));
                            tempData = ApiHandler.setJsonTagValue("event.scenarioData[" + i + "].duration-ms", tempData,
                                    eElement.getAttribute("duration-ms"));
                            tempData = ApiHandler.setJsonTagValue("event.scenarioData[" + i + "].status", tempData,
                                    eElement.getAttribute("status"));
                            if (StringUtils.isNotBlank(triggeredAt)) {
                                triggeredAt = eElement.getAttribute("started-at");
                                tempData = ApiHandler.setJsonTagValue("event.triggered-at", tempData,
                                        eElement.getAttribute("started-at"));
                            }
                            NewJsonData = "";
                            NewJsonData = tempData.toString();
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error("* Error reading dashboard data from file " + filename);
        }
        return tempData;
    }

    @SuppressWarnings("unchecked")
    private static void addScenarioData(String[] data, org.json.JSONArray stepData1, String evidenceURL)
            throws JSONException {
        JSONObject appData = new JSONObject();
        appData.put("application", data[0]);
        appData.put("scenario", data[1]);
        appData.put("type", data[2]);
        appData.put("status", "");
        appData.put("started-at", "");
        appData.put("finished-at", "");
        appData.put("duration-ms", "");
        appData.put("steps", stepData1);
        if (StringUtils.isNotBlank(evidenceURL)) {
            appData.put("evidenceURL", evidenceURL);
        }
        scenarioData.add(appData);
    }

    @SuppressWarnings("unchecked")
    private static JSONObject addFeatureData(String env, String feature) {
        JSONObject envData = new JSONObject();
        envData.put("environment", env);
        envData.put("triggered-at", "");
        envData.put("business scenario", feature);
        envData.put("scenarioData", scenarioData);
        JSONObject tempObj = new JSONObject();
        tempObj.put("sourcetype", "E2ETestAutomation");
        tempObj.put("event", envData);
        return tempObj;
    }

    private static void sendDataToSvtSplunk(String payload, String filename) {
        LOG.info("* Sending data to SVT splunk...");
        RequestBody postRequestBody = RequestBody.create(MediaType.parse("application/json"), payload.getBytes());
        Request request = new Request.Builder().header("Authorization", "Splunk 72166d25-dc5c-47a2-8b7c-1cc22c30a3c3")
                .url("http://lxapp4346.dc.corp.dummycompany.com:8088/services/collector/event").post(postRequestBody)
                .build();
        Response response = ApiHandler.getApiResponse(request);
        try {

            if (Objects.requireNonNull(response).code() == 200) {
                LOG.info("* Successfully sent data to svt splunk for " + filename);
            } else {
                LOG.error("* Received unexpected response code " + response.code()
                        + " while sending data to SVT splunk for " + filename);
            }
        } catch (Exception e) {
            LOG.error("* Error sending data to SVT splunk for file " + filename);
        }
        response.close();
    }

    private static void sendDataToNonProdBambiSplunk(String payload, String filename) {
        LOG.info("* Sending data to NON-PROD BAMBI splunk...");
        RequestBody postReqBody = RequestBody.create(MediaType.parse("application/json"), payload.getBytes());
        Request postRequest = new Request.Builder()
                .header("Authorization", "Splunk 0b37f176-55bc-4a57-9d78-ebe31c61f99f")
                .url("https://bambi-eventcollector-svt.wip.epic.dc.corp.dummycompany.com:8088/services/collector")
                .post(postReqBody).build();
        Response bsplunkResponse = ApiHandler.getApiResponse(postRequest);
        try {
            if (Objects.requireNonNull(bsplunkResponse).code() == 200) {
                LOG.info("* Successfully sent data to bambi splunk for " + filename);
            } else {
                LOG.error("* Received unexpected response code " + bsplunkResponse.code()
                        + " while sending data to NON-PROD BAMBI splunk for " + filename);
            }
        } catch (Exception e) {
            LOG.error("* Error sending data to NON-PROD BAMBI splunk for file " + filename);
        }
        bsplunkResponse.close();
    }

    private static void sendDataToProdBambiSplunk(String payload, String filename) {
        LOG.info("* Sending data to PROD BAMBI splunk...");
        RequestBody postBambiReqBody = RequestBody.create(MediaType.parse("application/json"), payload.getBytes());
        Request postBambiRequest = new Request.Builder()
                .header("Authorization", "Splunk 0b37f176-55bc-4a57-9d78-ebe31c61f99f")
                .url("https://bambi.eventcollector-int.dc.corp.dummycompany.com:8088/services/collector")
                .post(postBambiReqBody).build();
        Response bambiSplunkResponse = ApiHandler.getApiResponse(postBambiRequest);
        try {
//            String apiResponse = bambiSplunkResponse.body().string();
            if (Objects.requireNonNull(bambiSplunkResponse).code() == 200) {
                LOG.info("* Successfully sent data to PROD BAMBI splunk for " + filename);
            } else {
                LOG.error("* Received unexpected response code " + bambiSplunkResponse.code()
                        + " while sending data to PROD BAMBI splunk for " + filename);
            }
        } catch (Exception e) {
            LOG.error("* Error sending data to PROD BAMBI splunk for file " + filename);
        }
        bambiSplunkResponse.close();
    }

    private static String getStartedAtFromXML(String scenarioName, String filename) {
        String startedAt = "";
        try {
            File fXmlFile = new File("./target/cucumber-parallel/testng/" + filename.replace(".json", ".xml"));
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("test-method");
            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                if (Objects.requireNonNull(nNode).getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String sceName = eElement.getAttribute("name");
                    for (int i = 0; i < scenarioCount; i++) {
                        String tempSceName = scenarioName;
                        if (sceName.contains(tempSceName)) {
                            if (StringUtils.isNotBlank(triggeredAt)) {
                                startedAt = eElement.getAttribute("started-at");
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(" Error getStartedAtFromXML from file " + filename);
        }
        return startedAt;
    }

    private static String getStepTimeStamp(String timeStamp, Object stepDuration, String stepStatus) {
        try {
            if (stepStatus.equalsIgnoreCase("failed") || stepStatus.equalsIgnoreCase("passed")) {
                stepDurationInMs = TimeUnit.NANOSECONDS.toMillis((long) stepDuration);
                DateFormat dateFormat1 = new SimpleDateFormat("SSS");
                stepDate = dateFormat1.parse(stepDurationInMs + "");
                DateFormat formatter1 = new SimpleDateFormat("HH:mm:ssss");
                stepDateString = formatter1.format(stepDate);
                DateFormat stepDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssss");
                Date stepDate = stepDateFormat.parse(timeStamp);
                DateFormat stepFormatter = new SimpleDateFormat("HH:mm:ss");
                String toDateStr = stepFormatter.format(stepDate);
                SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm:ssss");
                timeformat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date toDate1 = timeformat.parse(toDateStr);
                Date toDate2 = timeformat.parse(stepDateString);
                long sum = toDate1.getTime() + toDate2.getTime();
                stepsTimeStamp = timeformat.format(new Date(sum));
            } else {
                stepsTimeStamp = null;
            }
        } catch (ParseException e) {
            LOG.error("---- In getStampStamp ---- " + e);
        }
        return stepsTimeStamp;
    }
}
