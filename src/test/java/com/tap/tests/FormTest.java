package com.tap.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.tap.framework.base.BaseTest;
import com.tap.framework.pages.FormPage;
import com.tap.tests.data.TestDataProviders;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.testng.asserts.SoftAssert;
import org.testng.annotations.Test;

/** Data driven coverage of the "Form" widget. */
public class FormTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, dataProvider = "formDataExcel",
            dataProviderClass = TestDataProviders.class,
            description = "Fill the practice form with Excel driven data and verify every field")
    public void shouldFillFormWithExcelData(Map<String, String> data) {
        FormPage form = homePage.form()
                .enterName(data.get("name"))
                .enterEmail(data.get("email"))
                .enterPhone(data.get("phone"))
                .enterAddress(data.get("address"))
                .selectGender(data.get("gender"))
                .selectDays(split(data.get("days")))
                .selectCountry(data.get("country"))
                .selectColors(split(data.get("colors")));

        SoftAssert softly = new SoftAssert();
        softly.assertEquals(form.getName(), data.get("name"), "name");
        softly.assertEquals(form.getEmail(), data.get("email"), "email");
        softly.assertEquals(form.getPhone(), data.get("phone"), "phone");
        softly.assertEquals(form.getAddress(), data.get("address"), "address");
        softly.assertTrue(form.isGenderSelected(data.get("gender")), "gender radio selected");
        split(data.get("days")).forEach(day ->
                softly.assertTrue(form.isDaySelected(day), "day checkbox selected: " + day));
        softly.assertEquals(form.getSelectedCountry(), data.get("country"), "country");
        softly.assertEquals(form.getSelectedColors(), split(data.get("colors")), "colors");
        softly.assertAll();
    }

    @Test(groups = "regression", dataProvider = "formDataJson",
            dataProviderClass = TestDataProviders.class,
            description = "Fill the practice form with JSON driven data")
    @SuppressWarnings("unchecked")
    public void shouldFillFormWithJsonData(Map<String, Object> data) {
        FormPage form = homePage.form()
                .enterName((String) data.get("name"))
                .enterEmail((String) data.get("email"))
                .enterPhone((String) data.get("phone"))
                .selectGender((String) data.get("gender"))
                .selectDays((List<String>) data.get("days"))
                .selectCountry((String) data.get("country"))
                .selectAnimals((List<String>) data.get("animals"));

        assertEquals(form.getName(), data.get("name"));
        assertEquals(form.getSelectedCountry(), data.get("country"));
        assertEquals(form.getSelectedAnimals(), data.get("animals"));
    }

    @Test(groups = "regression", description = "The name field must not accept more than 15 characters")
    public void shouldEnforceNameMaxLength() {
        FormPage form = homePage.form();
        assertEquals(form.getNameMaxLength(), 15, "maxlength attribute");
        form.enterName("ThisNameIsLongerThanFifteenCharacters");
        assertEquals(form.getName().length(), 15, "typed value is truncated to maxlength");
    }

    @Test(groups = "regression", description = "The country dropdown exposes all ten countries")
    public void shouldExposeAllCountryOptions() {
        List<String> options = homePage.form().getCountryOptions();
        assertEquals(options.size(), 10, "country option count");
        assertTrue(options.contains("India") && options.contains("United States"),
                "expected countries present, got " + options);
    }

    @Test(groups = "regression", description = "The sorted animal list is rendered alphabetically")
    public void shouldRenderAnimalsAlphabetically() {
        List<String> animals = homePage.form().getAnimalOptions();
        List<String> sorted = animals.stream().sorted(String.CASE_INSENSITIVE_ORDER).toList();
        assertEquals(animals, sorted, "animal options should be sorted");
    }

    private List<String> split(String value) {
        return value == null || value.isBlank()
                ? List.of()
                : Arrays.stream(value.split(",")).map(String::trim).filter(v -> !v.isEmpty()).toList();
    }
}
