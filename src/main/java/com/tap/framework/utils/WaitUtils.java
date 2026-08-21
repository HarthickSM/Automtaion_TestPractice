package com.tap.framework.utils;

import com.tap.framework.config.ConfigKey;
import com.tap.framework.config.ConfigReader;
import java.time.Duration;
import java.util.function.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Explicit wait helpers. Every wait ignores {@link StaleElementReferenceException} which removes
 * the majority of the flakiness caused by the JavaScript widgets on the practice site.
 */
public final class WaitUtils {

    private final WebDriver driver;
    private final Duration timeout;
    private final Duration polling;

    public WaitUtils(WebDriver driver) {
        this(driver, Duration.ofSeconds(ConfigReader.getLong(ConfigKey.EXPLICIT_WAIT)));
    }

    public WaitUtils(WebDriver driver, Duration timeout) {
        this.driver = driver;
        this.timeout = timeout;
        this.polling = Duration.ofMillis(ConfigReader.getLong(ConfigKey.POLLING_INTERVAL_MILLIS));
    }

    public WaitUtils withTimeout(Duration newTimeout) {
        return new WaitUtils(driver, newTimeout);
    }

    public <T> T until(Function<WebDriver, T> condition) {
        return newWait().until(condition);
    }

    public WebElement visible(By locator) {
        return until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement visible(WebElement element) {
        return until(ExpectedConditions.visibilityOf(element));
    }

    public WebElement present(By locator) {
        return until(ExpectedConditions.presenceOfElementLocated(locator));
    }

    public WebElement clickable(By locator) {
        return until(ExpectedConditions.elementToBeClickable(locator));
    }

    public WebElement clickable(WebElement element) {
        return until(ExpectedConditions.elementToBeClickable(element));
    }

    public boolean invisible(By locator) {
        return until(ExpectedConditions.invisibilityOfElementLocated(locator));
    }

    public boolean textToBe(By locator, String text) {
        return until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }

    public boolean urlContains(String fraction) {
        return until(ExpectedConditions.urlContains(fraction));
    }

    public boolean numberOfWindowsToBe(int windows) {
        return until(ExpectedConditions.numberOfWindowsToBe(windows));
    }

    public boolean elementCountAtLeast(By locator, int count) {
        return until(d -> d.findElements(locator).size() >= count);
    }

    public void documentReady() {
        until(d -> "complete".equals(new JavaScriptUtils(d).executeScript("return document.readyState")));
    }

    private FluentWait<WebDriver> newWait() {
        return new WebDriverWait(driver, timeout, polling)
                .ignoring(StaleElementReferenceException.class)
                .ignoring(NoSuchElementException.class);
    }
}
