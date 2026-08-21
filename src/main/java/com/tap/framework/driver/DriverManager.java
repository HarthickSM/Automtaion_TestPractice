package com.tap.framework.driver;

import com.tap.framework.exceptions.FrameworkException;
import org.openqa.selenium.WebDriver;

/**
 * Thread confined {@link WebDriver} holder so suites can run in parallel without tests sharing a
 * browser session.
 */
public final class DriverManager {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverManager() {
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            throw new FrameworkException("WebDriver has not been initialised for thread "
                    + Thread.currentThread().getName());
        }
        return driver;
    }

    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    public static void setDriver(WebDriver driver) {
        DRIVER.set(driver);
    }

    public static void unload() {
        DRIVER.remove();
    }
}
