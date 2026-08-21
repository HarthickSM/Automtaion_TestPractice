package com.tap.framework.utils;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

/**
 * Reusable, wait backed interaction layer shared by every page object. Page objects never talk to
 * {@link WebDriver} directly, which keeps them short and keeps retry/wait logic in one place.
 */
public class ElementActions {

    private static final Logger LOG = LogManager.getLogger(ElementActions.class);

    protected final WebDriver driver;
    protected final WaitUtils wait;
    protected final JavaScriptUtils js;
    protected final Actions actions;

    public ElementActions(WebDriver driver) {
        this.driver = driver;
        this.wait = new WaitUtils(driver);
        this.js = new JavaScriptUtils(driver);
        this.actions = new Actions(driver);
    }

    public WebDriver driver() {
        return driver;
    }

    public WaitUtils waits() {
        return wait;
    }

    public JavaScriptUtils js() {
        return js;
    }

    public Actions actions() {
        return actions;
    }

    // ------------------------------------------------------------------ basic

    public WebElement find(By locator) {
        return wait.present(locator);
    }

    public List<WebElement> findAll(By locator) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        return driver.findElements(locator);
    }

    public void click(By locator) {
        WebElement element = wait.clickable(locator);
        js.scrollIntoView(element);
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            LOG.warn("Native click intercepted on {}, falling back to JS click", locator);
            js.click(element);
        }
    }

    public void type(By locator, String text) {
        WebElement element = wait.visible(locator);
        js.scrollIntoView(element);
        element.clear();
        element.sendKeys(text);
    }

    public void typeAndEnter(By locator, String text) {
        type(locator, text);
        wait.visible(locator).sendKeys(Keys.ENTER);
    }

    public String getText(By locator) {
        return wait.visible(locator).getText().trim();
    }

    public String getAttribute(By locator, String attribute) {
        return wait.present(locator).getAttribute(attribute);
    }

    public String getValue(By locator) {
        return getAttribute(locator, "value");
    }

    public boolean isDisplayed(By locator) {
        try {
            return wait.withTimeout(Duration.ofSeconds(5)).visible(locator).isDisplayed();
        } catch (RuntimeException e) {
            return false;
        }
    }

    public boolean isSelected(By locator) {
        return wait.present(locator).isSelected();
    }

    public boolean isEnabled(By locator) {
        return wait.present(locator).isEnabled();
    }

    /** Ticks a checkbox / radio only when it is not already in the wanted state. */
    public void setChecked(By locator, boolean checked) {
        WebElement element = wait.clickable(locator);
        if (element.isSelected() != checked) {
            js.scrollIntoView(element);
            element.click();
        }
    }

    // --------------------------------------------------------------- dropdown

    public Select select(By locator) {
        return new Select(wait.visible(locator));
    }

    public void selectByVisibleText(By locator, String text) {
        select(locator).selectByVisibleText(text);
    }

    public void selectByValue(By locator, String value) {
        select(locator).selectByValue(value);
    }

    public void selectByIndex(By locator, int index) {
        select(locator).selectByIndex(index);
    }

    /**
     * Selects the first option whose text matches. Needed for lists that contain duplicated option
     * texts (the practice site's Colors list has two "Red" entries), where
     * {@link Select#selectByVisibleText(String)} would select every duplicate.
     */
    public void selectFirstOptionByText(By locator, String text) {
        Select select = select(locator);
        select.getOptions().stream()
                .filter(option -> option.getText().trim().equalsIgnoreCase(text.trim()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Option '" + text + "' not present in " + locator))
                .click();
    }

    public void deselectAll(By locator) {
        select(locator).deselectAll();
    }

    public List<String> getOptions(By locator) {
        return select(locator).getOptions().stream()
                .map(option -> option.getText().trim())
                .collect(Collectors.toList());
    }

    public List<String> getSelectedOptions(By locator) {
        return select(locator).getAllSelectedOptions().stream()
                .map(option -> option.getText().trim())
                .collect(Collectors.toList());
    }

    // ---------------------------------------------------------------- actions

    public void hover(By locator) {
        WebElement element = wait.visible(locator);
        js.scrollIntoView(element);
        actions.moveToElement(element).perform();
    }

    public void doubleClick(By locator) {
        WebElement element = wait.clickable(locator);
        js.scrollIntoView(element);
        actions.doubleClick(element).perform();
    }

    public void rightClick(By locator) {
        actions.contextClick(wait.clickable(locator)).perform();
    }

    /** HTML5 friendly drag and drop: a click-hold, an intermediate move, then a release. */
    public void dragAndDrop(By source, By target) {
        WebElement from = wait.visible(source);
        WebElement to = wait.visible(target);
        js.scrollIntoView(to);
        actions.clickAndHold(from)
                .moveToElement(to, 1, 1)
                .moveToElement(to)
                .release()
                .pause(Duration.ofMillis(300))
                .perform();
    }

    public void dragBy(By source, int xOffset, int yOffset) {
        WebElement element = wait.visible(source);
        js.scrollIntoView(element);
        actions.clickAndHold(element)
                .moveByOffset(xOffset, yOffset)
                .release()
                .pause(Duration.ofMillis(300))
                .perform();
    }

    // ----------------------------------------------------------------- alerts

    public Alert waitForAlert() {
        return wait.until(ExpectedConditions.alertIsPresent());
    }

    public String acceptAlert() {
        Alert alert = waitForAlert();
        String text = alert.getText();
        alert.accept();
        return text;
    }

    public String dismissAlert() {
        Alert alert = waitForAlert();
        String text = alert.getText();
        alert.dismiss();
        return text;
    }

    public String typeInAlertAndAccept(String text) {
        Alert alert = waitForAlert();
        String message = alert.getText();
        alert.sendKeys(text);
        alert.accept();
        return message;
    }

    // ---------------------------------------------------------------- windows

    /** Switches to the first window that is not the given one and returns its handle. */
    public String switchToNewWindow(String parentHandle) {
        wait.until(d -> d.getWindowHandles().size() > 1);
        String target = driver.getWindowHandles().stream()
                .filter(handle -> !handle.equals(parentHandle))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No new window was opened"));
        driver.switchTo().window(target);
        return target;
    }

    public void closeAndSwitchBack(String parentHandle) {
        driver.close();
        driver.switchTo().window(parentHandle);
    }

    /** Runs an element inside a shadow root; the site exposes open shadow roots only. */
    public WebElement shadowElement(By hostLocator, String cssInsideShadow) {
        WebElement host = wait.present(hostLocator);
        return (WebElement) ((JavascriptExecutor) driver)
                .executeScript("return arguments[0].shadowRoot.querySelector(arguments[1]);", host, cssInsideShadow);
    }
}
