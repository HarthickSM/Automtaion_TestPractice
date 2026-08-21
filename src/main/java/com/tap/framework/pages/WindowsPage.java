package com.tap.framework.pages;

import com.tap.framework.base.BasePage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** "New Tab" and "Popup Windows" buttons: window handle management. */
public class WindowsPage extends BasePage {

    private static final By NEW_TAB = By.xpath("//button[normalize-space()='New Tab']");
    private static final By POPUP = By.id("PopUp");

    public WindowsPage(WebDriver driver) {
        super(driver);
    }

    public String parentHandle() {
        return driver.getWindowHandle();
    }

    /** Clicks "New Tab", switches to it and returns the URL that was opened. */
    public String openNewTabAndGetUrl() {
        String parent = parentHandle();
        click(NEW_TAB);
        switchToNewWindow(parent);
        wait.until(d -> !"about:blank".equals(d.getCurrentUrl()));
        String url = driver.getCurrentUrl();
        closeAndSwitchBack(parent);
        return url;
    }

    /** Clicks "Popup Windows" (opens two windows) and returns every child window title. */
    public List<String> openPopupsAndGetTitles() {
        String parent = parentHandle();
        click(POPUP);
        wait.numberOfWindowsToBe(3);
        Set<String> handles = driver.getWindowHandles();
        List<String> titles = new ArrayList<>();
        for (String handle : handles) {
            if (handle.equals(parent)) {
                continue;
            }
            driver.switchTo().window(handle);
            wait.documentReady();
            titles.add(driver.getTitle());
            driver.close();
        }
        driver.switchTo().window(parent);
        return titles;
    }
}
