package com.tap.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import com.tap.framework.base.BaseTest;
import com.tap.framework.pages.TablesPage;
import com.tap.tests.data.TestDataProviders;
import java.util.List;
import java.util.Map;
import org.testng.annotations.Test;

/** Static, dynamic and paginated web tables. */
public class TablesTest extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "The static book table exposes six books")
    public void shouldReadStaticTable() {
        TablesPage tables = homePage.tables();
        assertTrue(tables.isBookTableDisplayed(), "book table displayed");
        assertEquals(tables.getBookTableHeaders(), List.of("BookName", "Author", "Subject", "Price"));
        assertEquals(tables.getBookCount(), 6, "book row count");
    }

    @Test(groups = "regression", dataProvider = "bookSearchData",
            dataProviderClass = TestDataProviders.class,
            description = "Books can be filtered by author (Excel driven)")
    public void shouldFilterBooksByAuthor(Map<String, String> data) {
        List<String> books = homePage.tables().getBooksByAuthor(data.get("author"));
        assertEquals(books.size(), Integer.parseInt(data.get("expectedCount")),
                "books for " + data.get("author") + ": " + books);
        assertTrue(books.contains(data.get("expectedBook")), "expected " + data.get("expectedBook") + " in " + books);
    }

    @Test(groups = "regression", description = "Total price of the Selenium books is 3300")
    public void shouldSumPricesBySubject() {
        assertEquals(homePage.tables().getTotalPriceOfBooksBySubject("Selenium"), 3300);
    }

    @Test(groups = "regression", description = "The dynamic table renders the process rows with values")
    public void shouldReadDynamicTable() {
        TablesPage tables = homePage.tables();
        List<String> headers = tables.getTaskTableHeaders();
        assertTrue(headers.size() >= 2, "dynamic table headers: " + headers);
        String cpu = tables.getTaskTableCell("Chrome", headers.get(1));
        assertTrue(cpu != null && !cpu.isBlank(), "Chrome value should be rendered, got '" + cpu + "'");
    }

    @Test(groups = {"smoke", "regression"}, description = "Every paginated product is reachable")
    public void shouldWalkPaginatedTable() {
        TablesPage tables = homePage.tables();
        assertTrue(tables.getPageCount() > 1, "expected more than one page");
        List<String> products = tables.getAllProductNames();
        assertEquals(products.size(), products.stream().distinct().count(), "duplicate products: " + products);
        assertTrue(products.stream().anyMatch(name -> name.equalsIgnoreCase("Smartphone")),
                "Smartphone missing from " + products);
    }
}
