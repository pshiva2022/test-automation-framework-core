package com.dummycompany.test.lib.api;

import com.dummycompany.test.framework.core.Constants;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Map;

public final class FreemarkerTemplateBuilder {

  private final Template template;

  private FreemarkerTemplateBuilder(Template template) {
    this.template = template;
  }

  public static FreemarkerTemplateBuilder fromTemplate(String templateFilePath) {
    try {
      Configuration cfg = getConfiguration();
      FileTemplateLoader templateLoader = new FileTemplateLoader(
          new File(Constants.CURRENT_DIR + templateFilePath.substring(0, templateFilePath.lastIndexOf("/"))));
      cfg.setTemplateLoader(templateLoader);

      Template template = cfg.getTemplate(templateFilePath.substring(templateFilePath.lastIndexOf("/") + 1));

      return new FreemarkerTemplateBuilder(template);
    } catch (IOException e) {
      throw new RuntimeException("Failed to create freemarker template", e);
    }
  }

  private static Configuration getConfiguration() {
    Configuration cfg = new Configuration(Configuration.VERSION_2_3_23);
    cfg.setDefaultEncoding("UTF-8");
    return cfg;
  }

  public String build() {
    return build(Collections.EMPTY_MAP);
  }

  public String build(Object dataModel) {
    try {
      StringWriter writer = new StringWriter();
      template.process(dataModel, writer);
      return writer.toString();
    } catch (Exception e) {
      throw new RuntimeException("Failed to process freemarker template", e);
    }
  }
}
