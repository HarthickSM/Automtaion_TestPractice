package com.tap.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.tap.framework.base.BaseTest;
import com.tap.framework.pages.ElementsPage;
import java.util.List;
import org.testng.annotations.Test;

/** Labels, links, the auto complete combo box and shadow DOM content. */
public class ElementsTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "The home page loads with its heading")
    public void shouldLoadHomePage() {
        assertTrue(homePage.isLoaded(), "home page heading is not visible");
        assertTrue(homePage.getTitle().contains("Automation"), "unexpected title: " + homePage.getTitle());
    }

    @Test(groups = "regression", description = "Mobile labels and laptop links are rendered")
    public void shouldReadLabelsAndLinks() {
        ElementsPage page = homePage.elements();
        assertEquals(page.getMobileLabels(), List.of("Samsung", "Real Me", "Moto"));
        assertEquals(page.getLaptopLinkTexts(), List.of("Apple", "Lenovo", "Dell"));
        assertTrue(page.getLaptopLinkUrls().stream().allMatch(url -> url.startsWith("https://")),
                "all laptop links should be https: " + page.getLaptopLinkUrls());
    }

    @Test(groups = "regression", description = "The combo box lists its options and keeps the picked one")
    public void shouldSelectComboBoxOption() {
        ElementsPage page = homePage.elements();
        List<String> options = page.openComboBox();
        assertTrue(options.contains("Item 7"), "missing option: " + options);
        assertEquals(page.selectComboBoxOption("Item 7"), "Item 7");
    }

    @Test(groups = "regression", description = "Shadow DOM and nested shadow DOM text can be read")
    public void shouldReadShadowDomContent() {
        ElementsPage page = homePage.elements();
        assertEquals(page.getShadowContentText(), "Mobiles");
        assertTrue(page.getNestedShadowContentText().contains("Laptops"),
                "nested shadow text: " + page.getNestedShadowContentText());
    }

    @Test(groups = "regression", description = "Each footer form section keeps its own paragraph")
    public void shouldSubmitFooterSections() {
        ElementsPage page = homePage.elements();
        assertEquals(page.submitFooterSection(1, "hello"), "This is a paragraph in Section 1.");
        assertEquals(page.submitFooterSection(2, "world"), "This is a paragraph in Section 2.");
    }
}
