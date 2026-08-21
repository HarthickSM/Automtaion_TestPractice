package com.tap.framework.utils;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/** Thin wrapper around {@link JavascriptExecutor} for the operations the site needs. */
public final class JavaScriptUtils {

    private final JavascriptExecutor executor;

    public JavaScriptUtils(WebDriver driver) {
        this.executor = (JavascriptExecutor) driver;
    }

    public Object executeScript(String script, Object... args) {
        return executor.executeScript(script, args);
    }

    public void click(WebElement element) {
        executor.executeScript("arguments[0].click();", element);
    }

    public void scrollIntoView(WebElement element) {
        executor.executeScript("arguments[0].scrollIntoView({block:'center', inline:'center'});", element);
    }

    public void scrollToBottom() {
        executor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    public void setValue(WebElement element, String value) {
        executor.executeScript("arguments[0].value = arguments[1];", element, value);
    }

    public String getText(WebElement element) {
        return String.valueOf(executor.executeScript("return arguments[0].textContent;", element)).trim();
    }

    public void highlight(WebElement element) {
        executor.executeScript("arguments[0].style.border='3px solid red';", element);
    }
}
