package com.tap.framework.base;

import com.tap.framework.config.ConfigKey;
import com.tap.framework.config.ConfigReader;
import com.tap.framework.utils.ElementActions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;

/**
 * Parent of every page object. It supplies the shared interaction layer and navigation, so page
 * objects only declare locators plus business methods.
 */
public abstract class BasePage extends ElementActions {

    protected final Logger log = LogManager.getLogger(getClass());

    protected BasePage(WebDriver driver) {
        super(driver);
    }

    public void openBaseUrl() {
        driver.get(ConfigReader.get(ConfigKey.BASE_URL));
        wait.documentReady();
    }

    public String getTitle() {
        return driver.getTitle();
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
