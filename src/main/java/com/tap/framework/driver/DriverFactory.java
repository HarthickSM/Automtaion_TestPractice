package com.tap.framework.driver;

import com.tap.framework.config.ConfigKey;
import com.tap.framework.config.ConfigReader;
import com.tap.framework.constants.FrameworkConstants;
import com.tap.framework.exceptions.FrameworkException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;

/**
 * Creates browser sessions for local runs and for a Selenium Grid ({@code run.mode=remote}).
 * Driver binaries are resolved by Selenium Manager, so no binary has to be checked in.
 */
public final class DriverFactory {

    private static final Logger LOG = LogManager.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        return createDriver(BrowserType.from(ConfigReader.get(ConfigKey.BROWSER)));
    }

    public static WebDriver createDriver(BrowserType browser) {
        boolean remote = "remote".equalsIgnoreCase(ConfigReader.get(ConfigKey.RUN_MODE));
        WebDriver driver = remote ? createRemoteDriver(browser) : createLocalDriver(browser);
        applyTimeouts(driver);
        if (ConfigReader.getBoolean(ConfigKey.WINDOW_MAXIMIZE)) {
            driver.manage().window().maximize();
        }
        LOG.info("Started {} session ({})", browser, remote ? "remote" : "local");
        return driver;
    }

    private static WebDriver createLocalDriver(BrowserType browser) {
        return switch (browser) {
            case CHROME -> new ChromeDriver(chromeOptions());
            case FIREFOX -> new FirefoxDriver(firefoxOptions());
            case EDGE -> new EdgeDriver(edgeOptions());
        };
    }

    private static WebDriver createRemoteDriver(BrowserType browser) {
        MutableCapabilities options = switch (browser) {
            case CHROME -> chromeOptions();
            case FIREFOX -> firefoxOptions();
            case EDGE -> edgeOptions();
        };
        try {
            return new RemoteWebDriver(new URL(ConfigReader.get(ConfigKey.GRID_URL)), options);
        } catch (MalformedURLException e) {
            throw new FrameworkException("Invalid grid.url: " + ConfigReader.get(ConfigKey.GRID_URL), e);
        }
    }

    private static ChromeOptions chromeOptions() {
        ChromeOptions options = new ChromeOptions();
        if (isHeadless()) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-gpu",
                "--remote-allow-origins=*", "--disable-notifications", "--disable-search-engine-choice-screen");
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("download.default_directory", FrameworkConstants.DOWNLOAD_DIR);
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("credentials_enable_service", false);
        options.setExperimentalOption("prefs", prefs);
        return options;
    }

    private static FirefoxOptions firefoxOptions() {
        FirefoxOptions options = new FirefoxOptions();
        if (isHeadless()) {
            options.addArguments("-headless", "--width=1920", "--height=1080");
        }
        options.addPreference("browser.download.dir", FrameworkConstants.DOWNLOAD_DIR);
        options.addPreference("browser.download.folderList", 2);
        options.addPreference("dom.webnotifications.enabled", false);
        return options;
    }

    private static EdgeOptions edgeOptions() {
        EdgeOptions options = new EdgeOptions();
        if (isHeadless()) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage", "--disable-notifications");
        return options;
    }

    private static void applyTimeouts(WebDriver driver) {
        WebDriver.Timeouts timeouts = driver.manage().timeouts();
        timeouts.implicitlyWait(Duration.ofSeconds(ConfigReader.getLong(ConfigKey.IMPLICIT_WAIT)));
        timeouts.pageLoadTimeout(Duration.ofSeconds(ConfigReader.getLong(ConfigKey.PAGE_LOAD_TIMEOUT)));
        timeouts.scriptTimeout(Duration.ofSeconds(ConfigReader.getLong(ConfigKey.SCRIPT_TIMEOUT)));
    }

    private static boolean isHeadless() {
        return ConfigReader.getBoolean(ConfigKey.HEADLESS);
    }
}
