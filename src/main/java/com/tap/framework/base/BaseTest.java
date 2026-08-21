package com.tap.framework.base;

import com.tap.framework.config.ConfigKey;
import com.tap.framework.config.ConfigReader;
import com.tap.framework.driver.BrowserType;
import com.tap.framework.driver.DriverFactory;
import com.tap.framework.driver.DriverManager;
import com.tap.framework.listeners.RetryTransformer;
import com.tap.framework.listeners.TestListener;
import com.tap.framework.pages.HomePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Every test class extends this: a fresh browser per test method (safe for parallel execution),
 * already pointing at the application under test.
 */
@Listeners({TestListener.class, RetryTransformer.class})
public abstract class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    protected WebDriver driver;
    protected HomePage homePage;

    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUp(@Optional("") String browserParam) {
        String browserName = browserParam == null || browserParam.isBlank()
                ? ConfigReader.get(ConfigKey.BROWSER)
                : browserParam;
        driver = DriverFactory.createDriver(BrowserType.from(browserName));
        DriverManager.setDriver(driver);
        homePage = new HomePage(driver);
        homePage.openBaseUrl();
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (DriverManager.hasDriver()) {
            try {
                DriverManager.getDriver().quit();
            } catch (RuntimeException e) {
                log.warn("Failed to quit driver cleanly: {}", e.getMessage());
            } finally {
                DriverManager.unload();
            }
        }
    }
}
