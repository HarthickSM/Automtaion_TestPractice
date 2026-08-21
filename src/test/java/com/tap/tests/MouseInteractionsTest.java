package com.tap.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import com.tap.framework.base.BaseTest;
import com.tap.framework.pages.MouseInteractionsPage;
import java.util.List;
import org.testng.annotations.Test;

/** Hover menus, double click, drag and drop, slider and the dynamic START/STOP button. */
public class MouseInteractionsTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "Hovering Point Me reveals the dropdown items")
    public void shouldRevealMenuOnHover() {
        assertEquals(homePage.mouseInteractions().hoverAndGetMenuItems(), List.of("Mobiles", "Laptops"));
    }

    @Test(groups = "regression", description = "Double clicking Copy Text copies field1 into field2")
    public void shouldCopyTextOnDoubleClick() {
        MouseInteractionsPage page = homePage.mouseInteractions();
        assertEquals(page.copyFieldByDoubleClick(), page.getField1Value());
    }

    @Test(groups = "regression", description = "The draggable box can be dropped on the target")
    public void shouldDragAndDrop() {
        assertEquals(homePage.mouseInteractions().dragAndDropAndGetDropText(), "Dropped!");
    }

    @Test(groups = "regression", description = "Moving the slider handle updates the price range")
    public void shouldMoveSlider() {
        MouseInteractionsPage page = homePage.mouseInteractions();
        String before = page.getSliderAmount();
        String after = page.moveSliderHandle(0, 40);
        assertNotEquals(after, before, "slider amount should change");
        assertTrue(after.startsWith("$"), "unexpected amount format: " + after);
    }

    @Test(groups = "regression", description = "The dynamic button toggles between START and STOP")
    public void shouldToggleDynamicButton() {
        MouseInteractionsPage page = homePage.mouseInteractions();
        assertEquals(page.getDynamicButtonText(), "START");
        assertEquals(page.toggleDynamicButton(), "STOP");
    }
}
