package com.tap.framework.pages;

import com.tap.framework.base.BasePage;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Mouse hover menu, double click copy, jQuery drag and drop and the price range slider. */
public class MouseInteractionsPage extends BasePage {

    private static final By HOVER_BUTTON = By.cssSelector("button.dropbtn");
    private static final By HOVER_MENU_ITEMS = By.cssSelector(".dropdown-content a");
    private static final By FIELD_1 = By.id("field1");
    private static final By FIELD_2 = By.id("field2");
    private static final By COPY_BUTTON = By.xpath("//button[normalize-space()='Copy Text']");
    private static final By DRAGGABLE = By.id("draggable");
    private static final By DROPPABLE = By.id("droppable");
    private static final By SLIDER_HANDLES = By.cssSelector("#slider-range .ui-slider-handle");
    private static final By SLIDER_AMOUNT = By.id("amount");
    private static final By START_BUTTON = By.cssSelector("button[onclick='toggleButton(this)']");

    public MouseInteractionsPage(WebDriver driver) {
        super(driver);
    }

    public List<String> hoverAndGetMenuItems() {
        hover(HOVER_BUTTON);
        wait.visible(HOVER_MENU_ITEMS);
        return findAll(HOVER_MENU_ITEMS).stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    public String copyFieldByDoubleClick() {
        doubleClick(COPY_BUTTON);
        wait.until(d -> !getValue(FIELD_2).isEmpty());
        return getValue(FIELD_2);
    }

    public String getField1Value() {
        return getValue(FIELD_1);
    }

    public String dragAndDropAndGetDropText() {
        dragAndDrop(DRAGGABLE, DROPPABLE);
        return getText(DROPPABLE);
    }

    public String getSliderAmount() {
        return getValue(SLIDER_AMOUNT);
    }

    /** Moves the left ({@code index 0}) or right ({@code index 1}) slider handle horizontally. */
    public String moveSliderHandle(int index, int xOffset) {
        List<WebElement> handles = findAll(SLIDER_HANDLES);
        WebElement handle = handles.get(index);
        js.scrollIntoView(handle);
        String before = getSliderAmount();
        actions.clickAndHold(handle).moveByOffset(xOffset, 0).release().perform();
        wait.until(d -> !getSliderAmount().equals(before));
        return getSliderAmount();
    }

    public String toggleDynamicButton() {
        String before = getText(START_BUTTON);
        click(START_BUTTON);
        wait.until(d -> !getText(START_BUTTON).equals(before));
        return getText(START_BUTTON);
    }

    public String getDynamicButtonText() {
        return getText(START_BUTTON);
    }
}
