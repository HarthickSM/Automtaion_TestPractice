package com.tap.framework.driver;

import com.tap.framework.exceptions.FrameworkException;
import java.util.Arrays;

/** Browsers supported by {@link DriverFactory}. */
public enum BrowserType {

    CHROME,
    FIREFOX,
    EDGE;

    public static BrowserType from(String value) {
        return Arrays.stream(values())
                .filter(browser -> browser.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new FrameworkException("Unsupported browser: " + value));
    }
}
