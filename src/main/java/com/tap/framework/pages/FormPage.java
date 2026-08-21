package com.tap.framework.pages;

import com.tap.framework.base.BasePage;
import com.tap.framework.healing.SmartBy;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * "Form" widget: text inputs, radio buttons, check boxes and the three select lists. Locators are
 * {@link SmartBy}, so a renamed id is repaired from the declared fallback or from the stored
 * element fingerprint instead of failing the test.
 */
public class FormPage extends BasePage {

    private static final By NAME =
            SmartBy.of("form.name", By.id("name"), By.cssSelector("input[placeholder='Enter Name']"));
    private static final By EMAIL =
            SmartBy.of("form.email", By.id("email"), By.cssSelector("input[placeholder='Enter EMail']"));
    private static final By PHONE =
            SmartBy.of("form.phone", By.id("phone"), By.cssSelector("input[placeholder='Enter Phone']"));
    private static final By ADDRESS =
            SmartBy.of("form.address", By.id("textarea"), By.cssSelector("textarea"));
    private static final By COUNTRY =
            SmartBy.of("form.country", By.id("country"), By.cssSelector("select#country"));
    private static final By COLORS =
            SmartBy.of("form.colors", By.id("colors"), By.cssSelector("select[multiple]#colors"));
    private static final By ANIMALS =
            SmartBy.of("form.animals", By.id("animals"), By.cssSelector("select[multiple]#animals"));

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
