package com.tap.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.tap.framework.base.BaseTest;
import com.tap.framework.pages.DatePickerPage;
import org.testng.annotations.Test;

/** jQuery UI and native HTML5 date pickers. */
public class DatePickerTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "A date can be typed into date picker 1")
    public void shouldTypeDate() {
        DatePickerPage page = homePage.datePicker();
        page.typeDate("09/15/2025");
        assertEquals(page.getTypedDate(), "09/15/2025");
    }

    @Test(groups = "regression", description = "A date can be picked from the calendar of date picker 2")
    public void shouldPickDateFromCalendar() {
        DatePickerPage page = homePage.datePicker();
        page.pickDate("December", "2026", "25");
        assertEquals(page.getPickedDate(), "25/12/2026");
    }

    @Test(groups = "regression", description = "Date picker 3 reports the number of days in the range")
    public void shouldCalculateDateRange() {
        DatePickerPage page = homePage.datePicker();
        page.selectRange("2025-01-01", "2025-01-11");
        assertEquals(page.getRangeResult(), "You selected a range of 10 days.");
    }

    @Test(groups = "regression", description = "An end date before the start date is rejected")
    public void shouldRejectInvertedDateRange() {
        DatePickerPage page = homePage.datePicker();
        page.selectRange("2025-02-10", "2025-02-01");
        assertTrue(page.getRangeResult().contains("End date must be after start date"),
                "unexpected message: " + page.getRangeResult());
    }
}
