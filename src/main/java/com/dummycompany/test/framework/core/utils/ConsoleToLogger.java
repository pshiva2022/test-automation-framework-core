package com.dummycompany.test.framework.core.utils;

import com.dummycompany.test.framework.core.Constants;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

public class ConsoleToLogger extends OutputStream {

  private final Logger logger;
  private final Level logLevel;
  private final OutputStream outputStream;

  public ConsoleToLogger(Logger logger, Level logLevel, OutputStream outputStream) {
    super();
    this.logger = logger;
    this.logLevel = logLevel;
    this.outputStream = outputStream;
  }

  @Override
  public void write(byte[] b) throws IOException {
    outputStream.write(b);
    String string = new String(b);
    logConsoleString(StringEscapeUtils.escapeJava(string));
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    outputStream.write(b, off, len);
    String string = new String(b, off, len);
    logConsoleString(string);
  }

  @Override
  public void write(int b) throws IOException {
    outputStream.write(b);
    String string = String.valueOf(b);
    logConsoleString(string);
  }

  private void logConsoleString(String stringToFormat) {
    String string = stringToFormat.replaceAll("\u001B", "")
        .replaceAll("3*m", "")
        .replaceAll("0m", "");
    if (!string.trim().isEmpty()) {
      if (Constants.isValidEnvFound && string.trim().equals("0 Scenarios")) {
        String msg = "No matching scenarios found for given tag " + Props.getEnvOrPropertyValue("cucumber.filter.tags", "");
        logger.log(Level.ERROR, msg);
        throw new IllegalArgumentException(msg);
      } else {
        logger.log(logLevel, string);
      }
    }
  }
}