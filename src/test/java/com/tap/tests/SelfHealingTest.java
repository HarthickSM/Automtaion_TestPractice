package com.tap.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

import com.tap.framework.base.BaseTest;
import com.tap.framework.healing.HealingLog;
import com.tap.framework.healing.SmartBy;
import com.tap.framework.pages.FormPage;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

/**
 * The self healing locator layer. Each test breaks the page on purpose (the ids are rewritten with
 * JavaScript) and then asserts the locator still resolves to the right element.
 */
public class SelfHealingTest extends BaseTest {

    @Test(groups = {"smoke", "regression"},
            description = "A broken primary locator is healed from the declared fallback locator")
    public void shouldHealFromFallbackLocator() {
        By smart = SmartBy.of("healing.fallback.name", By.id("name-does-not-exist"),
                By.cssSelector("input[placeholder='Enter Name']"));
        homePage.type(smart, "Fallback healed");
        assertEquals(homePage.getValue(By.id("name")), "Fallback healed");
        assertTrue(HealingLog.events().stream().anyMatch(event -> event.contains("healing.fallback.name")),
                "no healing event was recorded: " + HealingLog.events());
    }

    @Test(groups = "regression",
            description = "A renamed id is healed from the fingerprint stored on the previous run")
    public void shouldHealFromStoredFingerprint() {
        By smart = SmartBy.of("healing.fingerprint.email", By.id("email"));
        homePage.type(smart, "first@run.com");

        homePage.js().executeScript("document.getElementById('email').id = 'email_v2';");

        homePage.type(smart, "healed@run.com");
        WebElement renamed = homePage.find(By.id("email_v2"));
        assertEquals(renamed.getAttribute("value"), "healed@run.com");
        assertTrue(HealingLog.events().stream()
                        .anyMatch(event -> event.contains("healing.fingerprint.email")),
                "no healing event was recorded: " + HealingLog.events());
    }

    @Test(groups = "regression",
            description = "A locator with no match and no similar element still fails the test")
    public void shouldNotHealWhenNothingIsSimilar() {
        By smart = SmartBy.of("healing.unknown.widget", By.id("widget-that-never-existed"));
        assertThrows(TimeoutException.class,
                () -> homePage.waits().withTimeout(Duration.ofSeconds(2)).present(smart));
    }

    @Test(groups = "regression",
            description = "The form still fills in completely after its ids are rewritten")
    public void shouldFillFormAfterIdsAreRenamed() {
        homePage.js().executeScript(
                "document.getElementById('name').id = 'name_2';"
                        + "document.getElementById('email').id = 'email_2';");

        FormPage form = homePage.form();
        form.enterName("Renamed Ids").enterEmail("renamed@ids.com");

        assertEquals(homePage.getValue(By.id("name_2")), "Renamed Ids");
        assertEquals(homePage.getValue(By.id("email_2")), "renamed@ids.com");
    }
}
