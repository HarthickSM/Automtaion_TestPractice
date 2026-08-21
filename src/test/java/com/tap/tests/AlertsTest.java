package com.tap.tests;

import static org.testng.Assert.assertEquals;

import com.tap.framework.base.BaseTest;
import com.tap.framework.pages.AlertsPage;
import org.testng.annotations.Test;

/** JavaScript alert, confirm and prompt handling. */
public class AlertsTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "Simple alert shows the expected message")
    public void shouldHandleSimpleAlert() {
        assertEquals(homePage.alerts().triggerSimpleAlertAndAccept(), "I am an alert box!");
    }

    @Test(groups = "regression", description = "Accepting the confirm alert reports OK")
    public void shouldAcceptConfirmAlert() {
        AlertsPage alerts = homePage.alerts();
        assertEquals(alerts.triggerConfirmAlert(true), "Press a button!");
        assertEquals(alerts.getResultText(), "You pressed OK!");
    }

    @Test(groups = "regression", description = "Dismissing the confirm alert reports Cancel")
    public void shouldDismissConfirmAlert() {
        AlertsPage alerts = homePage.alerts();
        alerts.triggerConfirmAlert(false);
        assertEquals(alerts.getResultText(), "You pressed Cancel!");
    }

    @Test(groups = "regression", description = "Prompt alert echoes the entered name")
    public void shouldHandlePromptAlert() {
        AlertsPage alerts = homePage.alerts();
        assertEquals(alerts.triggerPromptAlert("Devin"), "Please enter your name:");
        assertEquals(alerts.getResultText(), "Hello Devin! How are you today?");
    }

    @Test(groups = "regression", description = "Cancelling the prompt reports the cancellation")
    public void shouldHandleDismissedPromptAlert() {
        AlertsPage alerts = homePage.alerts();
        alerts.triggerPromptAlertAndDismiss();
        assertEquals(alerts.getResultText(), "User cancelled the prompt.");
    }
}
