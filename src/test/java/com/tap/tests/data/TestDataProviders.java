package com.tap.tests.data;

import com.tap.framework.utils.DataProviderUtils;
import com.tap.framework.utils.ExcelUtils;
import com.tap.framework.utils.JsonUtils;
import java.util.List;
import java.util.Map;
import org.testng.annotations.DataProvider;

/** Central data providers: Excel and JSON are both supported so the same tests can be fed from
 * whichever source a team prefers. */
public class TestDataProviders {

    @DataProvider(name = "formDataExcel")
    public Object[][] formDataExcel() {
        return DataProviderUtils.toObjectArray(ExcelUtils.read("FormTestData.xlsx", "FormData"));
    }

    @DataProvider(name = "formDataJson")
    public Object[][] formDataJson() {
        List<Map<String, Object>> rows = JsonUtils.readList("formData.json");
        return DataProviderUtils.toObjectArray(rows);
    }

    @DataProvider(name = "bookSearchData")
    public Object[][] bookSearchData() {
        return DataProviderUtils.toObjectArray(ExcelUtils.read("FormTestData.xlsx", "BookData"));
    }
}
