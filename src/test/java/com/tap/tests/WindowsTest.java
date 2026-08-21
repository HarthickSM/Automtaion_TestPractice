package com.tap.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.tap.framework.base.BaseTest;
import java.util.List;
import org.testng.annotations.Test;

/** Tab and popup window handling. */
public class WindowsTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "The New Tab button opens pavantestingtools.com")
    public void shouldOpenNewTab() {
        String url = homePage.windows().openNewTabAndGetUrl();
        assertTrue(url.contains("pavantestingtools.com"), "unexpected tab url: " + url);
        assertEquals(driver.getWindowHandles().size(), 1, "child tab should be closed again");
    }

    @Test(groups = "regression", description = "The popup button opens the Selenium and Playwright windows")
    public void shouldOpenTwoPopupWindows() {
        List<String> titles = homePage.windows().openPopupsAndGetTitles();
        assertEquals(titles.size(), 2, "expected two popup windows, got " + titles);
        assertTrue(titles.stream().anyMatch(title -> title.contains("Selenium")), "Selenium window: " + titles);
        assertTrue(titles.stream().anyMatch(title -> title.contains("Playwright")), "Playwright window: " + titles);
    }
}
