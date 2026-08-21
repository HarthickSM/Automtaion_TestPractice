package com.tap.framework.pages;

import com.tap.framework.base.BasePage;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** "Upload Files" widget: single and multiple file selection. */
public class FileUploadPage extends BasePage {

    private static final By SINGLE_INPUT = By.id("singleFileInput");
    private static final By SINGLE_SUBMIT = By.cssSelector("#singleFileForm button[type='submit']");
    private static final By SINGLE_STATUS = By.id("singleFileStatus");
    private static final By MULTIPLE_INPUT = By.id("multipleFilesInput");
    private static final By MULTIPLE_SUBMIT = By.cssSelector("#multipleFilesForm button[type='submit']");
    private static final By MULTIPLE_STATUS = By.id("multipleFilesStatus");

    public FileUploadPage(WebDriver driver) {
        super(driver);
    }

    public String uploadSingleFile(Path file) {
        find(SINGLE_INPUT).sendKeys(file.toAbsolutePath().toString());
        click(SINGLE_SUBMIT);
        wait.until(d -> !getText(SINGLE_STATUS).isEmpty());
        return getText(SINGLE_STATUS);
    }

    public String uploadMultipleFiles(List<Path> files) {
        String paths = files.stream()
                .map(file -> file.toAbsolutePath().toString())
                .collect(Collectors.joining("\n"));
        find(MULTIPLE_INPUT).sendKeys(paths);
        click(MULTIPLE_SUBMIT);
        wait.until(d -> !getText(MULTIPLE_STATUS).isEmpty());
        return getText(MULTIPLE_STATUS);
    }
}
