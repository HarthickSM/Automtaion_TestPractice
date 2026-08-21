package com.tap.framework.pages;

import com.tap.framework.base.BasePage;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Labels, links (including the broken link section), the auto complete combo box, the multi
 * section form in the footer and the shadow DOM widget. */
public class ElementsPage extends BasePage {

    private static final By MOBILE_LABELS = By.cssSelector("#mobiles label");
    private static final By LAPTOP_LINKS = By.cssSelector("#laptops a.link");
    private static final By BROKEN_LINKS = By.cssSelector("#broken-links a.link");
    private static final By COMBO_BOX = By.id("comboBox");
    private static final By COMBO_OPTIONS = By.cssSelector("#dropdown div");
    private static final By SHADOW_HOST = By.id("shadow_host");

    public ElementsPage(WebDriver driver) {
        super(driver);
    }

    public List<String> getMobileLabels() {
        return findAll(MOBILE_LABELS).stream()
                .map(label -> label.getText().trim())
                .collect(Collectors.toList());
    }

    public List<String> getLaptopLinkTexts() {
        return findAll(LAPTOP_LINKS).stream()
                .map(link -> link.getText().trim())
                .collect(Collectors.toList());
    }

    public List<String> getLaptopLinkUrls() {
        return findAll(LAPTOP_LINKS).stream()
                .map(link -> link.getAttribute("href"))
                .collect(Collectors.toList());
    }

    public int getBrokenLinkSectionSize() {
        return findAll(BROKEN_LINKS).size();
    }

    /** Sends a HEAD request to every link in the "Broken Links" block and returns the bad ones. */
    public List<String> findBrokenLinks() {
        List<String> broken = new ArrayList<>();
        for (WebElement link : findAll(BROKEN_LINKS)) {
            String url = link.getAttribute("href");
            try {
                HttpURLConnection connection = (HttpURLConnection) URI.create(url).toURL().openConnection();
                connection.setRequestMethod("HEAD");
                connection.setConnectTimeout(10_000);
                connection.setReadTimeout(10_000);
                connection.connect();
                if (connection.getResponseCode() >= 400) {
                    broken.add(url + " -> " + connection.getResponseCode());
                }
                connection.disconnect();
            } catch (Exception e) {
                broken.add(url + " -> " + e.getClass().getSimpleName());
            }
        }
        return broken;
    }

    /** Focuses the combo box so its lazily rendered option list is built, and returns the options. */
    public List<String> openComboBox() {
        click(COMBO_BOX);
        wait.elementCountAtLeast(COMBO_OPTIONS, 1);
        return findAll(COMBO_OPTIONS).stream()
                .map(option -> option.getText().trim())
                .collect(Collectors.toList());
    }

    public String selectComboBoxOption(String visibleText) {
        click(By.xpath("//div[@id='dropdown']//div[normalize-space()='" + visibleText + "']"));
        return getValue(COMBO_BOX);
    }

    public String submitFooterSection(int sectionNumber, String text) {
        type(By.id("input" + sectionNumber), text);
        click(By.id("btn" + sectionNumber));
        return getText(By.id("para" + sectionNumber));
    }

    public String getShadowContentText() {
        return shadowElement(SHADOW_HOST, "#shadow_content").getText().trim();
    }

    public String getNestedShadowContentText() {
        WebElement nestedHost = shadowElement(SHADOW_HOST, "#nested_shadow_host");
        return (String) js.executeScript(
                "return arguments[0].shadowRoot.querySelector('#nested_shadow_content').textContent;", nestedHost);
    }
}
