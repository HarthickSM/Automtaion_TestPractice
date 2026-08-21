package com.tap.framework.pages;

import com.tap.framework.base.BasePage;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Static "BookTable", the dynamic task table and the paginated product table. */
public class TablesPage extends BasePage {

    private static final By BOOK_TABLE = By.cssSelector("table[name='BookTable']");
    private static final By BOOK_ROWS = By.cssSelector("table[name='BookTable'] tr");
    private static final By BOOK_HEADERS = By.cssSelector("table[name='BookTable'] th");
    private static final By TASK_TABLE_HEADERS = By.cssSelector("#taskTable thead th");
    private static final By TASK_TABLE_ROWS = By.cssSelector("#taskTable tbody tr");
    private static final By PRODUCT_ROWS = By.cssSelector("#productTable tbody tr");
    private static final By PAGINATION_LINKS = By.cssSelector("#pagination li a");

    public TablesPage(WebDriver driver) {
        super(driver);
    }

    // -------------------------------------------------------------- static table

    public List<String> getBookTableHeaders() {
        return textOf(findAll(BOOK_HEADERS));
    }

    /** @return every data row as header-to-value pairs. */
    public List<Map<String, String>> getBookTableRows() {
        List<String> headers = getBookTableHeaders();
        List<Map<String, String>> rows = new ArrayList<>();
        List<WebElement> tableRows = findAll(BOOK_ROWS);
        for (WebElement row : tableRows) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (cells.isEmpty()) {
                continue;
            }
            Map<String, String> record = new LinkedHashMap<>();
            for (int i = 0; i < headers.size() && i < cells.size(); i++) {
                record.put(headers.get(i), cells.get(i).getText().trim());
            }
            rows.add(record);
        }
        return rows;
    }

    public int getBookCount() {
        return getBookTableRows().size();
    }

    public List<String> getBooksByAuthor(String author) {
        return getBookTableRows().stream()
                .filter(row -> row.get("Author").equalsIgnoreCase(author))
                .map(row -> row.get("BookName"))
                .toList();
    }

    public int getTotalPriceOfBooksBySubject(String subject) {
        return getBookTableRows().stream()
                .filter(row -> row.get("Subject").equalsIgnoreCase(subject))
                .mapToInt(row -> Integer.parseInt(row.get("Price")))
                .sum();
    }

    public boolean isBookTableDisplayed() {
        return isDisplayed(BOOK_TABLE);
    }

    // ------------------------------------------------------------- dynamic table

    public List<String> getTaskTableHeaders() {
        wait.elementCountAtLeast(TASK_TABLE_HEADERS, 1);
        return textOf(findAll(TASK_TABLE_HEADERS));
    }

    public String getTaskTableCell(String rowName, String columnName) {
        List<String> headers = getTaskTableHeaders();
        int column = headers.indexOf(columnName);
        if (column < 0) {
            throw new IllegalArgumentException("Unknown column '" + columnName + "' in " + headers);
        }
        for (WebElement row : findAll(TASK_TABLE_ROWS)) {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            if (!cells.isEmpty() && cells.get(0).getText().trim().equalsIgnoreCase(rowName)) {
                return cells.get(column).getText().trim();
            }
        }
        throw new IllegalArgumentException("Row '" + rowName + "' not found in the dynamic table");
    }

    // ---------------------------------------------------------- pagination table

    public int getPageCount() {
        return findAll(PAGINATION_LINKS).size();
    }

    public TablesPage goToPage(int pageNumber) {
        click(By.xpath("//ul[@id='pagination']//a[normalize-space()='" + pageNumber + "']"));
        wait.elementCountAtLeast(PRODUCT_ROWS, 1);
        return this;
    }

    public List<String> getProductNamesOnCurrentPage() {
        return findAll(PRODUCT_ROWS).stream()
                .map(row -> row.findElements(By.tagName("td")).get(1).getText().trim())
                .toList();
    }

    /** Walks every pagination page and collects the product names. */
    public List<String> getAllProductNames() {
        List<String> names = new ArrayList<>();
        int pages = getPageCount();
        for (int page = 1; page <= pages; page++) {
            goToPage(page);
            names.addAll(getProductNamesOnCurrentPage());
        }
        return names;
    }

    public double getTotalPriceOnCurrentPage() {
        return findAll(PRODUCT_ROWS).stream()
                .mapToDouble(row -> Double.parseDouble(
                        row.findElements(By.tagName("td")).get(2).getText().replace("$", "").trim()))
                .sum();
    }

    private List<String> textOf(List<WebElement> elements) {
        return elements.stream().map(element -> element.getText().trim()).toList();
    }
}
