package com.tap.framework.pages;

import com.tap.framework.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Entry point page object. The practice site is a single page, so this page exposes the header
 * checks and hands out the specialised widget page objects.
 */
public class HomePage extends BasePage {

    private static final By PAGE_HEADER = By.cssSelector("h1.title, h1.post-title, .post-title");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        return isDisplayed(PAGE_HEADER);
    }

    public FormPage form() {
        return new FormPage(driver);
    }

    public DatePickerPage datePicker() {
        return new DatePickerPage(driver);
    }

    public AlertsPage alerts() {
        return new AlertsPage(driver);
    }

    public WindowsPage windows() {
        return new WindowsPage(driver);
    }

    public MouseInteractionsPage mouseInteractions() {
        return new MouseInteractionsPage(driver);
    }

    public TablesPage tables() {
        return new TablesPage(driver);
    }

    public FileUploadPage fileUpload() {
        return new FileUploadPage(driver);
    }

    public ElementsPage elements() {
        return new ElementsPage(driver);
    }
}
