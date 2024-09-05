package com.dummycompany.test.framework.core.newreport;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import java.io.File;
import java.io.IOException;

public class GetFreeMarkerConfiguration {

  private static Configuration configuration;

  private GetFreeMarkerConfiguration() {
  }

  public static Configuration getConfiguration() {
    if (configuration == null) {
      configuration = new Configuration(Configuration.VERSION_2_3_23);
      configuration.setClassForTemplateLoading(GetFreeMarkerConfiguration.class, "/");
      configuration.setDefaultEncoding("UTF-8");
      configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
      configuration.setLogTemplateExceptions(false);
    }
    return configuration;
  }
}
