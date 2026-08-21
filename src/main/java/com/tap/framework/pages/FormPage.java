package com.tap.framework.pages;

import com.tap.framework.base.BasePage;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** "Form" widget: text inputs, radio buttons, check boxes and the three select lists. */
public class FormPage extends BasePage {

    private static final By NAME = By.id("name");
    private static final By EMAIL = By.id("email");
    private static final By PHONE = By.id("phone");
    private static final By ADDRESS = By.id("textarea");
    private static final By COUNTRY = By.id("country");
    private static final By COLORS = By.id("colors");
    private static final By ANIMALS = By.id("animals");

    public FormPage(WebDriver driver) {
        super(driver);
    }

    public FormPage enterName(String name) {
        type(NAME, name);
        return this;
    }

    public FormPage enterEmail(String email) {
        type(EMAIL, email);
        return this;
    }

    public FormPage enterPhone(String phone) {
        type(PHONE, phone);
        return this;
    }

    public FormPage enterAddress(String address) {
        type(ADDRESS, address);
        return this;
    }

    /** @param gender {@code male} or {@code female} */
    public FormPage selectGender(String gender) {
        click(By.id(gender.toLowerCase()));
        return this;
    }

    public FormPage selectDays(List<String> days) {
        days.stream()
                .map(day -> day.trim().toLowerCase())
                .filter(day -> !day.isEmpty())
                .forEach(day -> setChecked(By.id(day), true));
        return this;
    }

    public FormPage selectCountry(String visibleText) {
        selectByVisibleText(COUNTRY, visibleText);
        return this;
    }

    public FormPage selectColors(List<String> colors) {
        colors.stream()
                .map(String::trim)
                .filter(color -> !color.isEmpty())
                .forEach(color -> selectFirstOptionByText(COLORS, color));
        return this;
    }

    public FormPage selectAnimals(List<String> animals) {
        animals.stream()
                .map(String::trim)
                .filter(animal -> !animal.isEmpty())
                .forEach(animal -> selectByVisibleText(ANIMALS, animal));
        return this;
    }

    public String getName() {
        return getValue(NAME);
    }

    public String getEmail() {
        return getValue(EMAIL);
    }

    public String getPhone() {
        return getValue(PHONE);
    }

    public String getAddress() {
        return getValue(ADDRESS);
    }

    public boolean isGenderSelected(String gender) {
        return isSelected(By.id(gender.toLowerCase()));
    }

    public boolean isDaySelected(String day) {
        return isSelected(By.id(day.toLowerCase()));
    }

    public String getSelectedCountry() {
        return getSelectedOptions(COUNTRY).get(0);
    }

    public List<String> getSelectedColors() {
        return getSelectedOptions(COLORS);
    }

    public List<String> getSelectedAnimals() {
        return getSelectedOptions(ANIMALS);
    }

    public List<String> getCountryOptions() {
        return getOptions(COUNTRY);
    }

    public List<String> getAnimalOptions() {
        return getOptions(ANIMALS);
    }

    public int getNameMaxLength() {
        return Integer.parseInt(getAttribute(NAME, "maxlength"));
    }
}
