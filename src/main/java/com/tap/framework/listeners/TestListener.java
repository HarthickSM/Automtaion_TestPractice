package com.tap.framework.listeners;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.tap.framework.config.ConfigKey;
import com.tap.framework.config.ConfigReader;
import com.tap.framework.healing.HealingLog;
import com.tap.framework.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Drives the Extent report: one node per test method, screenshots attached on failure and, when
 * configured, on success.
 */
public class TestListener implements ITestListener {

    private static final Logger LOG = LogManager.getLogger(TestListener.class);

    @Override
    public void onStart(ITestContext context) {
        LOG.info("===== Starting <test> {} =====", context.getName());
        ExtentManager.getReporter();
    }

    @Override
    public void onTestStart(ITestResult result) {
        LOG.info("---> {}", testName(result));
        ExtentManager.createTest(testName(result), result.getMethod().getDescription());
        ExtentManager.getTest().assignCategory(result.getTestContext().getName());
        HealingLog.clear();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOG.info("PASSED  {}", testName(result));
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            if (ConfigReader.getBoolean(ConfigKey.SCREENSHOT_ON_PASS)) {
                attachScreenshot(test, Status.PASS, testName(result));
            } else {
                test.log(Status.PASS, "Test passed");
            }
            attachHealingEvents(test);
        }
        ExtentManager.unload();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("FAILED  {}", testName(result), result.getThrowable());
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.FAIL, result.getThrowable());
            if (ConfigReader.getBoolean(ConfigKey.SCREENSHOT_ON_FAILURE)) {
                attachScreenshot(test, Status.FAIL, testName(result));
            }
            attachHealingEvents(test);
        }
        ExtentManager.unload();
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOG.warn("SKIPPED {}", testName(result));
        ExtentTest test = ExtentManager.getTest();
        if (test != null) {
            test.log(Status.SKIP, result.getThrowable() == null
                    ? "Test skipped" : result.getThrowable().getMessage());
        }
        ExtentManager.unload();
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flush();
        LOG.info("===== Finished <test> {} : passed={} failed={} skipped={} =====", context.getName(),
                context.getPassedTests().size(), context.getFailedTests().size(),
                context.getSkippedTests().size());
    }

    private void attachHealingEvents(ExtentTest test) {
        HealingLog.events().forEach(event -> test.log(Status.WARNING, event));
        HealingLog.clear();
    }

    private void attachScreenshot(ExtentTest test, Status status, String name) {
        String base64 = ScreenshotUtils.captureAsBase64();
        String file = ScreenshotUtils.capture(name);
        if (base64 != null) {
            test.log(status, "Screenshot",
                    MediaEntityBuilder.createScreenCaptureFromBase64String(base64).build());
        }
        if (file != null) {
            LOG.info("Screenshot saved to {}", file);
        }
    }

    private String testName(ITestResult result) {
        Object[] parameters = result.getParameters();
        String base = result.getMethod().getMethodName();
        return parameters.length == 0 ? base : base + " " + shortParams(parameters);
    }

    private String shortParams(Object[] parameters) {
        String text = java.util.Arrays.toString(parameters);
        return text.length() > 80 ? text.substring(0, 77) + "...]" : text;
    }
}
