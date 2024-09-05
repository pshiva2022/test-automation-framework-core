package com.dummycompany.test.framework.core.report;

import com.dummycompany.test.framework.core.Constants;
import com.dummycompany.test.framework.core.db.DBConnectionUtils;
import com.dummycompany.test.framework.core.utils.Props;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlParser {

  private static final Logger LOG = LogManager.getLogger(HtmlParser.class);

  public static void main(String args[]) {
    generateEmailReport();
  }

  public static void generateEmailReport() {
    if (!Constants.PROFILE.equals("local")) {
      LOG.info("Trying to generate Email html report...");
      try {
        String inputFilePath = "target/cucumber-html-reports/overview-features.html";
        String outputFilePath = "target/featureReport.html";
        Document document = Jsoup.parse(new File(inputFilePath), "utf-8");

        for (Element element : document.select("script")) {
          element.remove();
        }

        for (Element element : document.select("link")) {
          element.remove();
        }

        //Remove navigation
        Elements navigation = document.select("#navigation");
        navigation.get(0).remove();

        //Remove charts
        Elements charts = document.select("#charts");
        charts.get(0).remove();

        Elements ele_thead_tr = document.select("#tablesorter > thead > tr");
        ele_thead_tr.get(0).remove();

        Elements ele_thead_tr_upd = document.select("#tablesorter > thead > tr");
        ele_thead_tr_upd.get(0).attr("class", "header dont-sort");

        Elements ele_thead_th = document.select("#tablesorter > thead > tr").get(0).select("th");
        ele_thead_th.get(1).remove();
        ele_thead_th.get(2).remove();
        ele_thead_th.get(3).remove();
        ele_thead_th.get(4).remove();
        ele_thead_th.get(5).remove();
        ele_thead_th.get(6).remove();
        ele_thead_th.get(7).remove();
        ele_thead_th.get(8).remove();
        ele_thead_th.get(9).remove();
//            document.select("#tablesorter > thead > tr").append("<th>Potential Failure Reason</th>");

        int rows = document.select("#tablesorter > tbody > tr").size();
        int featurePassed = 0;
        int featureFailed = 0;
        for (int i = 0; i < rows; i++) {
          Elements ele_tbody_td = document.select("#tablesorter > tbody > tr").get(i).select("td");
          ele_tbody_td.get(1).remove();
          ele_tbody_td.get(2).remove();
          ele_tbody_td.get(3).remove();
          ele_tbody_td.get(4).remove();
          ele_tbody_td.get(5).remove();
          ele_tbody_td.get(6).remove();
          ele_tbody_td.get(7).remove();
          ele_tbody_td.get(8).remove();
          ele_tbody_td.get(9).remove();

            if (ele_tbody_td.get(11).text().equalsIgnoreCase("Passed")) {
                featurePassed = featurePassed + 1;
            }

            if (ele_tbody_td.get(11).text().equalsIgnoreCase("Failed")) {
                featureFailed = featureFailed + 1;
            }
        }

        //Update href tag for links
        for (Element element : document.select("#tablesorter > tbody > tr > td >a")) {
          element.attr("href", "#");
        }

        Elements ele_tfoot_td = document.select("#tablesorter > tfoot");
        ele_tfoot_td.get(0).remove();

        Elements footer = document.select("#footer");
        footer.get(0).remove();

        StringBuilder styleHtml = new StringBuilder();
        styleHtml.append("<style type=\"text/css\">");
        styleHtml.append(".container-fluid{padding-right:15px;padding-left:15px;margin-right:auto;margin-left:auto}");
        styleHtml.append(".col-md-5 {width: 25.66666667%;float: left;position: relative;min-height: 1px;padding-right: 15px;}");
        styleHtml.append(".col-md-offset-1 {margin-left: 8.33333333%;margin-right: 8.33333333%;}");
        styleHtml.append(
            ".col-md-3 {width: 25%;float: left;position: relative;min-height: 1px;padding-right: 15px;padding-left: 15px;}");
        styleHtml.append(".col-md-offset-2 {margin-left: 8.33333333%;margin-right: 8.33333333%;}");
        styleHtml.append(".info{background-color:#d9edf7}");
        styleHtml.append(
            ".body{font-family: \"Segoe UI\",Tahoma, sans-serif;font-size: 11px;line-height: 1.42857143;color: #333;background-color: #fff;}");
        styleHtml.append(
            "html{font-size: 9px;color: #6c757d;-webkit-tap-highlight-color: rgba(0,0,0,0);font-family: Segoe UI;-webkit-text-size-adjust:");
        styleHtml.append("100%;-ms-text-size-adjust: 100%;color: -internal-root-color;}");
        styleHtml.append(".table {width: 30%; max-width: 30%; border-collapse: collapse;}");
        styleHtml.append(".table-bordered {border: 1px solid black;}");
        styleHtml.append("body {padding-top: 60px;}");
        styleHtml.append("h2 {font-size: 12px;}");
        styleHtml.append("a {color: #0097da;}");
        styleHtml.append("a:hover {color: #00587f;}");
        styleHtml.append(".header-tag-name {color: gray;font-style: italic;}");
        styleHtml.append(".keyword {font-weight: bold;}");
        styleHtml.append(".indention {padding-left: 3px;}");
        styleHtml.append(".inner-level {margin-top: 5px;margin-left: 20px;padding-bottom: 2px;padding-left: 1px;}");
        styleHtml.append(".element {margin-bottom: 15px;padding-left: 3px;}");
        styleHtml.append(".element, .steps, .hooks-after, .hooks-before {box-shadow: -1px 0 lightgray;transition: box-shadow 0.3s;}");
        styleHtml.append(".element:hover, .steps:hover, .hooks-after:hover, .hooks-before:hover {box-shadow: -3px 0 #6ce;}");
        styleHtml.append(".description {font-style: italic;background-color: beige;white-space: pre;}");
        styleHtml.append(".message, .output, .embedding {background-color: #dfdfdf;overflow: auto;}");
        styleHtml.append(
            ".embedding-content {padding: 10px;margin-left: 10px;margin-right: 10px;margin-bottom: 10px;font-size: 12px;overflow-x: auto;");
        styleHtml.append(
            "line-height: 1.42857143;color: #333;word-break: break-all;word-wrap: break-word;background-color: #f5f5f5;border: 1px solid #ccc;border-radius: 4px;}");
        styleHtml.append(".html-content {position: relative;padding: 0 0 56.25%;height: 0;overflow: hidden;}");
        styleHtml.append(".html-content iframe {position: absolute;top: 0;left: 0;width: 100%;height: 100%;border:none;}");
        styleHtml.append(".download-button {float: right;margin-right: 10px;color: #333;}");
        styleHtml.append(".passed {background-color: #92DD96;}");
        styleHtml.append(".failed {background-color: #F2928C;}");
        styleHtml.append(".skipped {background-color: #8AF;}");
        styleHtml.append(".pending {background-color: #F5F28F;}");
        styleHtml.append(".undefined {background-color: #F5B975;}");
        styleHtml.append(".lead-duration {float: right;padding-right: 15px;}");
        styleHtml.append(
            ".stats-table {background-color: white;color: #6c757d;margin-bottom: 20px;width: 100%;border-collapse: collapse;}");
        styleHtml.append(".stats-table th, .stats-table td {border: 1px solid gray;padding: 5px;text-align: left;}");
        styleHtml.append(".header {background-color: #17a2b8; color: white;}");
        styleHtml.append("table.stats-table tfoot {font-weight: bold;}");
        styleHtml.append(".stats-table .total {background-color: #D3D3D3;}");
        styleHtml.append(".stats-table .duration {text-align: right;white-space: nowrap;}");
        styleHtml.append(".tagname {text-align: left;}");
        styleHtml.append("table.stats-table td.location, .location {font-family: monospace;text-align: left;}");
        styleHtml.append("table.step-arguments {margin-bottom: 5px;margin-left: 25px;margin-top: 3px;}");
        styleHtml.append("table.step-arguments th, table.step-arguments td {border: 1px solid gray;padding: 3px;text-align: left;}");
        styleHtml.append("table#tablesorter thead tr:not(.dont-sort) th {cursor: pointer;}");
        styleHtml.append("tr:hover {transition: background-color 0.3s;}");
        styleHtml.append(".collapsable-control {cursor: pointer;}");
        styleHtml.append(".chevron:after {content: \"\\f078\";}");
        styleHtml.append(".collapsed .chevron:after {content: \"\\f054\";}");
        styleHtml.append(".footer {font-size: smaller;text-align: center;margin-top: 30px;}");
        styleHtml.append(".carousel-indicators {bottom: 0;}");
        styleHtml.append(".carousel-indicators li {border: 1px solid black;}");
        styleHtml.append(".carousel-indicators .active {background-color: black;}");
        styleHtml.append(".carousel-control {font-size: 14px;padding-top: 150px;}");
        styleHtml.append(".carousel-control.right, .carousel-control.left {background-image: none;color: #eee;}");
        styleHtml.append("pre {margin: 10px;}");
        styleHtml.append("</style>");

//        document.select("html > head").first().children().first().before(styleHtml.toString());
        Objects.requireNonNull(Objects.requireNonNull(document.select("html > head").first()).children().first()).before(styleHtml.toString());

        Elements featureText = document.getElementsByTag("p");
        featureText.get(0).text("The following table shows scenario wise script detailed execution status");

        int totalFeature = featureFailed + featurePassed;
        StringBuilder featureHtml = new StringBuilder();
        featureHtml.append("<tr class=\"info\"><th>Total Features</th><td>" + totalFeature + "</td></tr>");
        featureHtml.append("<tr class=\"info\"><th>Features Passed</th><td>" + featurePassed + "</td></tr>");
        featureHtml.append("<tr class=\"info\"><th>Features Failed</th><td>" + featureFailed + "</td></tr>");
        Objects.requireNonNull(document.select("#classifications > tbody").last().children().last()).after(featureHtml.toString());

        for (Element element : document.select("#build-info")) {
          element.attr("border", "1");
          element.attr("style", "border-color:#17a2b8;");
          element.attr("class", "table");
        }

        for (Element element : document.select("#classifications")) {
          element.attr("border", "1");
          element.attr("style", "border-color:#17a2b8;");
          element.attr("class", "table");
        }

        for (Element element : document.select("#tablesorter")) {
          element.attr("border", "1");
          element.attr("style", "text-align:left;");
        }

        Elements ele_build_info_tr_th = document.select("#build-info > thead > tr > th");
        ele_build_info_tr_th.get(1).remove();

        Elements ele_build_info_tr_td = document.select("#build-info > tbody > tr > td");
        ele_build_info_tr_td.get(1).remove();

        File newHtmlFile = new File(outputFilePath);
        FileUtils.writeStringToFile(newHtmlFile, document.html(), "utf-8");
        LOG.info("Successfully  generated Email html report...");
      } catch (Exception e) {
        LOG.error(ExceptionUtils.getStackTrace(e));
      }
    } else {
        LOG.warn("Skipping generation of emailable report in local profile");
    }
  }
}
