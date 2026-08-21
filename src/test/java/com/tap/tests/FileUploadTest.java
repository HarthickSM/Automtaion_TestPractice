package com.tap.tests;

import static org.testng.Assert.assertTrue;

import com.tap.framework.base.BaseTest;
import com.tap.framework.constants.FrameworkConstants;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** Single and multiple file upload. Fixtures are generated so the repository stays binary free. */
public class FileUploadTest extends BaseTest {

    private Path fileOne;
    private Path fileTwo;

    @BeforeClass(alwaysRun = true)
    public void createFixtures() throws IOException {
        Path directory = Path.of(FrameworkConstants.OUTPUT_DIR, "uploads");
        Files.createDirectories(directory);
        fileOne = Files.writeString(directory.resolve("upload-one.txt"), "selenium upload fixture one");
        fileTwo = Files.writeString(directory.resolve("upload-two.txt"), "selenium upload fixture two");
    }

    @Test(groups = {"smoke", "regression"}, description = "A single file can be uploaded")
    public void shouldUploadSingleFile() {
        String status = homePage.fileUpload().uploadSingleFile(fileOne);
        assertTrue(status.contains("upload-one.txt"), "unexpected status: " + status);
    }

    @Test(groups = "regression", description = "Multiple files can be uploaded at once")
    public void shouldUploadMultipleFiles() {
        String status = homePage.fileUpload().uploadMultipleFiles(List.of(fileOne, fileTwo));
        assertTrue(status.contains("2"), "expected two files in status, got: " + status);
        assertTrue(status.contains("upload-one.txt") && status.contains("upload-two.txt"),
                "unexpected status: " + status);
    }
}
