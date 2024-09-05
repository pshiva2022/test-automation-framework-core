package com.dummycompany.test.lib.api;

import static io.restassured.RestAssured.given;

import com.dummycompany.test.framework.core.context.World;
import com.dummycompany.test.framework.core.utils.Props;
import io.cucumber.java.Scenario;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.SSLConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.ProxySpecification;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;

public class RequestSpecificationFactory {

  private static final Logger LOG = LogManager.getLogger(RequestSpecificationFactory.class);
  private static final Boolean PROXY_REQUIRED = Boolean.valueOf(Props.getEnvOrPropertyValue("proxyRequired", "false"));
  private static final Boolean DAST_ENABLED = Boolean.valueOf(Props.getEnvOrPropertyValue("dastEnabled", "false"));

  public static RequestSpecification getInstance(World world) {
    return getInstance(world, PROXY_REQUIRED, true, DAST_ENABLED);
  }

  public static RequestSpecification getInstance(World world, boolean proxyRequired) {
    return getInstance(world, proxyRequired, true, DAST_ENABLED);
  }

  public static RequestSpecification getInstance(World world, boolean proxyRequired, boolean shouldLogRequestResponse) {
    return getInstance(world, PROXY_REQUIRED, shouldLogRequestResponse, DAST_ENABLED);
  }

  public static RequestSpecification getInstance(World world, boolean proxyRequired, boolean shouldLogRequestResponse,
      boolean dastEnabled) {
    System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2,TLSv1.3");
    RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
    if (proxyRequired) {
      LOG.info("Setting Proxy");
      ProxySpecification spec = ProxySpecification
          .host(Props.getEnvOrPropertyValue("proxyHostValue", "http-gw.tcif.dummycompany.com.au"))
          .withPort(Integer.parseInt(Props.getEnvOrPropertyValue("proxyPortValue", "8080")))
          .withScheme(Props.getEnvOrPropertyValue("proxySchemaValue", "http"))
          .withAuth(Props.getEnvOrPropertyValue("proxyUser"), Props.getEnvOrPropertyValue("proxyPassword"));
      requestSpecBuilder.setProxy(spec);
    }
    if (shouldLogRequestResponse) {
      requestSpecBuilder
          .addFilter(new CustomLogFilter(world.scenario, dastEnabled));
    }
    requestSpecBuilder.setConfig(RestAssured.config().sslConfig(new SSLConfig()
        .relaxedHTTPSValidation().allowAllHostnames()));
    return given(requestSpecBuilder.build());
  }

  public static RequestSpecification getInstanceWithDastDisabled(World world, boolean proxyRequired) {
    return getInstance(world, proxyRequired, true, false);
  }

  public static RequestSpecification getInstanceWithDastDisabled(World world) {
    return getInstance(world, false, true, false);
  }

  @Deprecated
  public static RequestSpecification getInstanceWithoutLogs(World world, boolean shouldLogRequestResponse) {
    throw new UnsupportedOperationException(
        " *** getInstanceWithoutLogs(World world, boolean shouldLogRequestResponse) is deprecated. Instead use getInstanceWithoutLogs(World world) ***");
  }

  public static RequestSpecification getInstanceWithoutLogs(World world) {
    return getInstance(world, PROXY_REQUIRED, false, DAST_ENABLED);
  }

  public static class CustomLogFilter implements Filter {

    private final Scenario scenario;
    private final Boolean dastEnabled;

    public CustomLogFilter(Scenario scenario, boolean dastEnabled) {
      this.scenario = scenario;
      this.dastEnabled = dastEnabled;
    }

    @Override
    public Response filter(FilterableRequestSpecification filterableRequestSpecification,
        FilterableResponseSpecification filterableResponseSpecification, FilterContext filterContext) {
      Response response = filterContext.next(filterableRequestSpecification, filterableResponseSpecification);
      String requestLogs = "Request : " +
          "\n" +
          "Request method: " + objectValidation(filterableRequestSpecification.getMethod()) +
          "\n" +
          "Request URI: " + objectValidation(filterableRequestSpecification.getURI()) +
          "\n" +
          "Form Params: " + objectValidation(filterableRequestSpecification.getFormParams()) +
          "\n" +
          "Request Param: " + objectValidation(filterableRequestSpecification.getRequestParams()) +
          "\n" +
          "Headers: " + objectValidation(filterableRequestSpecification.getHeaders()) +
          "\n" +
          "Cookies: " + objectValidation(filterableRequestSpecification.getCookies()) +
          "\n" +
          "Proxy: " + objectValidation(filterableRequestSpecification.getProxySpecification()) +
          "\n" +
          "Body: " + objectValidation(filterableRequestSpecification.getBody()) +
          "\n" +
          "******************************";
      String responseLogs = "\n" + "Response : " + "\n" +
          "Status Code: " + response.getStatusCode() +
          "\n" +
          "Status Line: " + response.getStatusLine() +
          "\n" +
          "Response Cookies: " + response.getDetailedCookies() +
          "\n" +
          "Response Content Type: " + response.getContentType() +
          "\n" +
          "Response Headers: " + response.getHeaders() +
          "\n" +
          "Response Body: " + "\n" + response.getBody().prettyPrint();
      scenario.log(requestLogs + responseLogs);
      LOG.info(requestLogs);
      LOG.info(responseLogs);
      if (dastEnabled) {
        response.getHeaders().forEach((header) -> {
          String headerName = header.getName().toLowerCase();
          boolean badHeaderPresent = headerName.equalsIgnoreCase("Server")
              || headerName.equalsIgnoreCase("X-Powered-By")
              || headerName.equalsIgnoreCase("X-AspNet-Version")
              || headerName.equalsIgnoreCase("X-AspNetMvc-Version")
              || headerName.equalsIgnoreCase("via");
          Assert.assertFalse(
              "DAST Violation : The response contains one of the bad headers('Server','X-Powered-By','X-AspNet-Version','X-AspNetMvc-Version','via' ). Please ask your api developers to fix the API response.",
              badHeaderPresent);
          if (headerName.equalsIgnoreCase("Access-Control-Allow-Origin")) {
            Assert.assertFalse(
                "DAST Violation : The response has 'Origin' header that contains '*' in value. Please ask your api developers to fix the API response.",
                header.getValue().contains("*"));
          }
        });
      }
      return response;
    }

    public String objectValidation(Object o) {
      if (o == null) {
        return null;
      } else {
        return o.toString();
      }
    }

  }
}