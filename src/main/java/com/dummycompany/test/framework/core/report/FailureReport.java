package com.dummycompany.test.framework.core.report;

import com.dummycompany.test.framework.core.db.DBConnection;
import org.json.JSONException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FailureReport {

    private static final Logger LOG = LogManager.getLogger(FailureReport.class);

    public static void main(String[] args) throws JSONException {
        List<String> arrData = getFailureReason();
        for (String num : arrData) {
            LOG.info("Data: " + num);
        }
    }

    public static List<String> getFailureReason() {
        List<String> arrData = new ArrayList<>();
        try {
            File folder = new File("./target/cucumber-parallel/testng/");
            File[] listOfFiles = folder.listFiles();

            for (int fn = 0; fn < Objects.requireNonNull(listOfFiles).length; fn++) {
                if (listOfFiles[fn].isFile()) {
                    String data = readXmlData(listOfFiles[fn].getName());
                    arrData.add(data);
                }
            }
        } catch (Exception e) {
            LOG.error("* Error while reading/sending data to dashboard");
        }
        return arrData;
    }

    private static String readXmlData(String filename) {
        String tempData = null;
        try {
            File fXmlFile = new File("./target/cucumber-parallel/testng/" + filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();

            Element eElement1 = (Element) doc.getElementsByTagName("class").item(0);
            String featureName = Objects.requireNonNull(eElement1).getAttribute("name");

            tempData = filename + ";" + featureName;
            String failureReason = "";

            NodeList nList_sce = doc.getElementsByTagName("test-method");
            int counter = 0;
            for (int temp = 0; temp < nList_sce.getLength(); temp++) {
                Node nNode_sce = nList_sce.item(temp);
                Element eElement = (Element) nNode_sce;
                String sceName = eElement.getAttribute("name");
                String sceStatus = eElement.getAttribute("status");
                if (sceName.contains("AEM") && sceStatus.contains("FAIL")) {
                    counter = 1;
                }
                if (!sceName.contains("AEM")) {
                    NodeList nList_failure = doc.getElementsByTagName("full-stacktrace");
                    for (int j = counter; j < nList_failure.getLength(); j++) {
                        Node nNode = nList_failure.item(j);

                        if (Objects.requireNonNull(nNode).getNodeType() == Node.ELEMENT_NODE) {
                            Element eElement_2 = (Element) nNode;
                            String errorMsg = eElement_2.getTextContent();
                            failureReason = readFailure(errorMsg);
                            // System.out.println(failureReason);
                            break;
                        }
                    }
                }

                if (!failureReason.isEmpty()) {
                    //	tempData = tempData + ";" + failureReason;
                    break;
                }
            }
            if(failureReason.isEmpty()){
                failureReason="NA";
            }
            tempData = tempData + ";" + failureReason;
        } catch (Exception e) {
            LOG.error("* Error reading xml files data from file " + filename);
        }
        return tempData;
    }

    private static String readFailure(String errorMsg) {
        String failureReason;

        if (errorMsg.contains("AssertionError")) {
            if (errorMsg.contains("Capturing EOMSYS Profile") ) {
                failureReason = "Order failed in EOMSYS";
            }
            else if (errorMsg.contains("Order is not present in EOMSYS")) {
                failureReason = "Order not present in EOMSYS, post submission";
            }
            else if (errorMsg.contains("Error while creating Siebel customer")) {
                failureReason = "Error creating Siebel customer";
            }
            else if (errorMsg.contains("Status of the MSISDN could not be changed in LRM")) {
                failureReason = "Not able to change MSISDN status in LRM";
            }
            else if (errorMsg.contains("Checkout Failed")) {
                failureReason = "TDM data checkout failed";
            }
            else if (errorMsg.contains("Error while getting SIM details from TDM")) {
                failureReason = "Error getting vacant SIM details from TDM API";
            }
            else if (errorMsg.contains("Failing scenario due to no available test data in TDM")) {
                failureReason = "Stage 1 data not available in databank";
            }
            else if (errorMsg.contains("LOMS Order is not generated from LISA Toll")) {
                failureReason = "Order not reached to LISA-TOLL from LOMS";
            }
            else if (errorMsg.contains("Could not get the Order ID from Salesforce to map in LISA")) {
                failureReason = "Order id not found from Salesforce";
            } else if (errorMsg.contains("Error while generating token for API call")) {
                failureReason = "Authentication Call Or Token Generation Failed ";
            } else if (errorMsg.contains("Error While triggering API under test")) {
                failureReason = "API trigger failed  - Needs further investigation ";
            } else if (errorMsg.contains("API response code is")) {
                failureReason = errorMsg.split("for triggered call")[0].split("junit.framework.AssertionFailedError:")[1];
            } else {
                failureReason = "Validation failed, need further investigation";
            }
        }
        else if (errorMsg.contains("ComparisonFailure")) {
            failureReason = "Application issue";
        }

        else if (errorMsg.contains("NoSuchElementException")) {
            failureReason = "Element not found-xpath changed or application issue";
        } else if (errorMsg.contains("Arity mismatch") || errorMsg.contains("ArrayIndexOutOfBoundsException") || errorMsg.contains("IllegalArgumentException")) {
            failureReason = "Scripting issue";
        }
        else if (errorMsg.contains("TimeoutException")) {
            failureReason = "Element timeout- need further investigation";
        }
        else {
            failureReason = "Need further investigation";
        }
        return failureReason;
    }
}