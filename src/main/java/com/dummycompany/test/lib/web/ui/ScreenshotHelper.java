package com.dummycompany.test.lib.web.ui;

import com.assertthat.selenium_shutterbug.core.Capture;
import com.assertthat.selenium_shutterbug.core.PageSnapshot;
import com.assertthat.selenium_shutterbug.core.Shutterbug;
import com.dummycompany.test.framework.core.context.World;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class ScreenshotHelper {

  private static final Logger LOG = LogManager.getLogger(ScreenshotHelper.class);

  public static void capture(World world, WebDriver driver) {
    if (null != driver) {
      try {
        world.scenario.attach(((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES), "image/png", "Screenshot");
      } catch (Exception e) {
        LOG.warn(e);
        captureFullPage(world, driver, Shutterbug.shootPage(driver, Capture.VIEWPORT));
      }
    }
  }

  public static void captureFullPage(World world, WebDriver driver) {
    try {
      captureFullPage(world, driver, Shutterbug.shootPage(driver, Capture.FULL));
    } catch (Exception e) {
      LOG.warn(e);
    }
  }

  public static void captureFullPageWithScroll(World world, WebDriver driver) {
    try {
      captureFullPage(world, driver, Shutterbug.shootPage(driver, Capture.FULL_SCROLL));
    } catch (Exception e) {
      LOG.warn(e);
    }
  }

  public static void captureFullPageWithVerticalScroll(World world, WebDriver driver) {
    try {
      captureFullPage(world, driver, Shutterbug.shootPage(driver, Capture.VERTICAL_SCROLL));
    } catch (Exception e) {
      LOG.warn(e);
    }
  }

  public static void captureFullPageWithHorizontalScroll(World world, WebDriver driver) {
    try {
      captureFullPage(world, driver, Shutterbug.shootPage(driver, Capture.HORIZONTAL_SCROLL));
    } catch (Exception e) {
      LOG.warn(e);
    }
  }

  public static void captureFullPage(World world, WebDriver driver, PageSnapshot pageSnapshot) {
    if (null != driver) {
      try {
        world.scenario.log("UrL: " + driver.getCurrentUrl());
        byte[] screenShot;
        BufferedImage image = pageSnapshot.getImage();
        screenShot = ScreenshotHelper.imageToByteArray(image);
        world.scenario.attach(screenShot, "image/png", "Screenshot");
      } catch (Exception e) {
        world.log(ExceptionUtils.getFullStackTrace(e));
      }
    }
  }

  public static byte[] imageToByteArray(BufferedImage image) throws IOException {
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    ImageIO.write(image, "png", stream);
    return stream.toByteArray();
  }
}