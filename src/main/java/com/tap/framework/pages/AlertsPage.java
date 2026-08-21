package com.tap.framework.pages;

import com.tap.framework.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** "Alerts &amp; Popups" widget: simple, confirmation and prompt alerts. */
public class AlertsPage extends BasePage {

    private static final By SIMPLE_ALERT = By.id("alertBtn");
    private static final By CONFIRM_ALERT = By.id("confirmBtn");
    private static final By PROMPT_ALERT = By.id("promptBtn");
    private static final By RESULT = By.id("demo");

    public AlertsPage(WebDriver driver) {
        super(driver);
    }

    public String triggerSimpleAlertAndAccept() {
        click(SIMPLE_ALERT);
        return acceptAlert();
    }

    public String triggerConfirmAlert(boolean accept) {
        click(CONFIRM_ALERT);
        return accept ? acceptAlert() : dismissAlert();
    }

    public String triggerPromptAlert(String name) {
        click(PROMPT_ALERT);
        return typeInAlertAndAccept(name);
    }

    public String triggerPromptAlertAndDismiss() {
        click(PROMPT_ALERT);
        return dismissAlert();
    }

    public String getResultText() {
        return getText(RESULT);
    }
}
