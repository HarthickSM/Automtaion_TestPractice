package com.tap.framework.listeners;

import com.tap.framework.config.ConfigKey;
import com.tap.framework.config.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/** Re-runs a failed test up to {@code retry.count} times to absorb genuine environment blips. */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final Logger LOG = LogManager.getLogger(RetryAnalyzer.class);
    private static final int MAX_RETRIES = Integer.parseInt(ConfigReader.get(ConfigKey.RETRY_COUNT));

    private int attempts = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (attempts < MAX_RETRIES) {
            attempts++;
            LOG.warn("Retrying {} (attempt {} of {})", result.getMethod().getMethodName(), attempts, MAX_RETRIES);
            return true;
        }
        return false;
    }
}
