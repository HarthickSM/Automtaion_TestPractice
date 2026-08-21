package com.tap.framework.config;

/** Type safe view over the keys present in {@code config.properties}. */
public enum ConfigKey {

    BASE_URL("base.url"),
    BROWSER("browser"),
    HEADLESS("headless"),
    RUN_MODE("run.mode"),
    GRID_URL("grid.url"),
    WINDOW_MAXIMIZE("window.maximize"),
    IMPLICIT_WAIT("implicit.wait"),
    EXPLICIT_WAIT("explicit.wait"),
    PAGE_LOAD_TIMEOUT("page.load.timeout"),
    SCRIPT_TIMEOUT("script.timeout"),
    POLLING_INTERVAL_MILLIS("polling.interval.millis"),
    REPORT_NAME("report.name"),
    REPORT_TITLE("report.title"),
    SCREENSHOT_ON_FAILURE("screenshot.on.failure"),
    SCREENSHOT_ON_PASS("screenshot.on.pass"),
    RETRY_COUNT("retry.count"),
    SELF_HEALING("self.healing"),
    SELF_HEALING_MIN_SCORE("self.healing.min.score");

    private final String key;

    ConfigKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
