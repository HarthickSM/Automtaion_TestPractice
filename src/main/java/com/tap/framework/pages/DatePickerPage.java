package com.tap.framework.pages;

import com.tap.framework.base.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** The three date pickers: jQuery UI (mm/dd/yyyy), read only jQuery UI (dd/mm/yyyy) and the
 * native HTML5 date range. */
public class DatePickerPage extends BasePage {

    private static final By DATE_PICKER_1 = By.id("datepicker");
    private static final By DATE_PICKER_2 = By.id("txtDate");
    private static final By CALENDAR = By.id("ui-datepicker-div");
    private static final By CALENDAR_TITLE = By.cssSelector("#ui-datepicker-div .ui-datepicker-title");
    private static final By MONTH_DROPDOWN = By.cssSelector("#ui-datepicker-div select.ui-datepicker-month");
    private static final By YEAR_DROPDOWN = By.cssSelector("#ui-datepicker-div select.ui-datepicker-year");
    private static final By START_DATE = By.id("start-date");
    private static final By END_DATE = By.id("end-date");
    private static final By RANGE_SUBMIT = By.xpath("//button[@onclick='calculateRange()']");
    private static final By RANGE_RESULT = By.id("result");

    public DatePickerPage(WebDriver driver) {
        super(driver);
    }

    public DatePickerPage typeDate(String mmddyyyy) {
        type(DATE_PICKER_1, mmddyyyy);
        return this;
    }

    public String getTypedDate() {
        return getValue(DATE_PICKER_1);
    }

    public DatePickerPage openCalendar() {
        click(DATE_PICKER_2);
        wait.visible(CALENDAR);
        return this;
    }

    public String getCalendarTitle() {
        return getText(CALENDAR_TITLE);
    }

    /**
     * Drives the read only field through the calendar month/year dropdowns and then clicks the
     * day cell, which is the reliable way to set a date the user cannot type.
     */
    public DatePickerPage pickDate(String month, String year, String day) {
        openCalendar();
        selectByValue(YEAR_DROPDOWN, year);
        selectByValue(MONTH_DROPDOWN, String.valueOf(monthIndex(month)));
        click(By.xpath("//div[@id='ui-datepicker-div']//a[normalize-space()='"
                + Integer.parseInt(day) + "']"));
        return this;
    }

    public String getPickedDate() {
        return getValue(DATE_PICKER_2);
    }

    public DatePickerPage selectRange(String startIsoDate, String endIsoDate) {
        js.setValue(find(START_DATE), startIsoDate);
        js.setValue(find(END_DATE), endIsoDate);
        click(RANGE_SUBMIT);
        return this;
    }

    public String getRangeResult() {
        return getText(RANGE_RESULT);
    }

    private int monthIndex(String monthName) {
        String[] months = {"January", "February", "March", "April", "May", "June", "July", "August",
                "September", "October", "November", "December"};
        for (int i = 0; i < months.length; i++) {
            if (months[i].equalsIgnoreCase(monthName)) {
                return i;
            }
        }
        throw new IllegalArgumentException("Unknown month: " + monthName);
    }
}
