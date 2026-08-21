package com.tap.framework.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.tap.framework.config.ConfigKey;
import com.tap.framework.config.ConfigReader;
import com.tap.framework.constants.FrameworkConstants;
import java.io.File;

/** Owns the single {@link ExtentReports} instance and the thread local current test node. */
public final class ExtentManager {

    private static final ThreadLocal<ExtentTest> CURRENT_TEST = new ThreadLocal<>();
    private static ExtentReports extentReports;

    private ExtentManager() {
    }

    public static synchronized ExtentReports getReporter() {
        if (extentReports == null) {
            String path = FrameworkConstants.REPORT_DIR + File.separator + ConfigReader.get(ConfigKey.REPORT_NAME);
            ExtentSparkReporter spark = new ExtentSparkReporter(path);
            spark.config().setTheme(Theme.DARK);
            spark.config().setDocumentTitle(ConfigReader.get(ConfigKey.REPORT_TITLE));
            spark.config().setReportName(ConfigReader.get(ConfigKey.REPORT_TITLE));
            extentReports = new ExtentReports();
            extentReports.attachReporter(spark);
            extentReports.setSystemInfo("Application", ConfigReader.get(ConfigKey.BASE_URL));
            extentReports.setSystemInfo("Browser", ConfigReader.get(ConfigKey.BROWSER));
            extentReports.setSystemInfo("Headless", ConfigReader.get(ConfigKey.HEADLESS));
            extentReports.setSystemInfo("Run mode", ConfigReader.get(ConfigKey.RUN_MODE));
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java", System.getProperty("java.version"));
        }
        return extentReports;
    }

    public static void createTest(String name, String description) {
        CURRENT_TEST.set(getReporter().createTest(name, description));
    }

    public static ExtentTest getTest() {
        return CURRENT_TEST.get();
    }

    public static void unload() {
        CURRENT_TEST.remove();
    }

    public static synchronized void flush() {
        if (extentReports != null) {
            extentReports.flush();
        }
    }
}
