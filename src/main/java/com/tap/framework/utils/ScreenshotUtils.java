package com.tap.framework.utils;

import com.tap.framework.constants.FrameworkConstants;
import com.tap.framework.driver.DriverManager;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

/** Saves screenshots to disk and produces base64 payloads for the Extent report. */
public final class ScreenshotUtils {

    private static final Logger LOG = LogManager.getLogger(ScreenshotUtils.class);
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    private ScreenshotUtils() {
    }

    /** @return absolute path of the saved png, or {@code null} when capture was not possible. */
    public static String capture(String testName) {
        if (!DriverManager.hasDriver()) {
            return null;
        }
        try {
            File source = ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.FILE);
            Path destination = Path.of(FrameworkConstants.SCREENSHOT_DIR,
                    testName.replaceAll("[^a-zA-Z0-9_.-]", "_") + "_" + LocalDateTime.now().format(STAMP) + ".png");
            Files.createDirectories(destination.getParent());
            FileUtils.copyFile(source, destination.toFile());
            return destination.toAbsolutePath().toString();
        } catch (Exception e) {
            LOG.warn("Could not capture screenshot for {}: {}", testName, e.getMessage());
            return null;
        }
    }

    public static String captureAsBase64() {
        if (!DriverManager.hasDriver()) {
            return null;
        }
        try {
            return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BASE64);
        } catch (Exception e) {
            LOG.warn("Could not capture base64 screenshot: {}", e.getMessage());
            return null;
        }
    }

    public static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
