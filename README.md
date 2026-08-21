# Automtaion_TestPractice

Hybrid (Page Object Model + data driven + keyword-style reusable actions) Selenium test automation
framework in Java for https://testautomationpractice.blogspot.com/.

## Tech stack

| Purpose | Library |
| --- | --- |
| Language | Java 17 |
| Build | Maven |
| Browser automation | Selenium 4.25 (Selenium Manager resolves drivers automatically) |
| Test runner | TestNG 7.10 |
| Reporting | ExtentReports 5 + TestNG reports |
| Logging | Log4j 2 |
| Test data | Apache POI (Excel) + Jackson (JSON) |

## Prerequisites

* JDK 17+ (`java -version`)
* Maven 3.6+ (`mvn -v`)
* Chrome (default), optionally Firefox / Edge

No driver binaries are needed; Selenium Manager downloads and caches the matching driver.

## Running the tests

```bash
# full regression suite (default suite in pom.xml)
mvn test

# smoke suite
mvn test -DsuiteXmlFile=src/test/resources/suites/smoke.xml

# cross browser smoke (Chrome + Firefox in parallel)
mvn test -DsuiteXmlFile=src/test/resources/suites/cross-browser.xml

# run a single test class / method
mvn test -Dtest=FormTest
mvn test -Dtest=FormTest#shouldFillFormWithExcelData
```

### Runtime overrides

Every key in `src/main/resources/config.properties` can be overridden with `-D`:

```bash
mvn test -Dbrowser=firefox -Dheadless=false
mvn test -Dbrowser=edge -Dexplicit.wait=30
mvn test -Dthreads=4                 # parallel method threads
```

Selenium Grid:

```bash
mvn test -Drun.mode=remote -Dgrid.url=http://localhost:4444/wd/hub -Dbrowser=chrome
```

## Reports and artifacts

| Artifact | Location |
| --- | --- |
| Extent HTML report | `test-output/reports/ExtentReport.html` |
| Failure screenshots | `test-output/screenshots/` |
| Log file | `test-output/logs/automation.log` |
| TestNG native report | `target/surefire-reports/` |

## Project layout

```
src/main/java/com/tap/framework
├── base          BasePage (shared page behaviour), BaseTest (driver lifecycle per test method)
├── config        ConfigKey enum + ConfigReader (file values, system property overrides)
├── constants     Output/report/screenshot/test-data paths
├── driver        BrowserType, DriverFactory (local + Grid), DriverManager (ThreadLocal driver)
├── exceptions    FrameworkException
├── listeners     TestListener, ExtentManager, RetryAnalyzer, RetryTransformer
├── pages         Page objects, one per widget group of the site
└── utils         WaitUtils, ElementActions, JavaScriptUtils, ScreenshotUtils,
                  ExcelUtils, JsonUtils, DataProviderUtils
src/test/java/com/tap/tests        TestNG tests + data providers
src/test/resources/suites          smoke.xml, regression.xml, cross-browser.xml
src/test/resources/testdata        FormTestData.xlsx, formData.json
```

### Design notes

* **Thread safe** – the driver lives in a `ThreadLocal`, so suites can run methods/classes in
  parallel; each test method gets a fresh browser and a fresh page load.
* **Reliable waits** – no `Thread.sleep`. `WaitUtils` wraps `FluentWait` with a polling interval and
  ignores `StaleElementReferenceException`/`NoSuchElementException`; `ElementActions` waits before
  every interaction and falls back to a JavaScript click when a native click is intercepted.
* **Retry** – `RetryTransformer` applies `RetryAnalyzer` to every test; `retry.count` controls it.
* **Data driven** – `TestDataProviders` feeds tests from Excel sheets and JSON files, so new cases
  are added by editing data, not code.

## Coverage

Form (text inputs, radio buttons, checkboxes, single/multi selects), date pickers (jQuery UI,
read-only jQuery UI with month/year dropdowns, HTML5 range), alerts (simple/confirm/prompt),
windows and popups, mouse interactions (hover menu, double click, drag & drop, slider, dynamic
START/STOP button), tables (static, dynamic, paginated), file upload (single + multiple), links and
broken links, scrollable combo box, footer form sections, shadow DOM and nested shadow DOM.

## Adding a test

1. Add or extend a page object in `com.tap.framework.pages` (locators as `private static final By`).
2. Expose it from `HomePage` if it is a new section.
3. Add a `@Test` in `com.tap.tests` with `groups = {"smoke", "regression"}` as appropriate.
4. Register new classes in `src/test/resources/suites/regression.xml`.
