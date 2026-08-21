package com.tap.framework.constants;

import java.io.File;

/** Single place for all non-configurable framework paths and defaults. */
public final class FrameworkConstants {

    private FrameworkConstants() {
    }

    public static final String USER_DIR = System.getProperty("user.dir");
    public static final String OUTPUT_DIR = USER_DIR + File.separator + "test-output";
    public static final String REPORT_DIR = OUTPUT_DIR + File.separator + "reports";
    public static final String SCREENSHOT_DIR = OUTPUT_DIR + File.separator + "screenshots";
    public static final String DOWNLOAD_DIR = OUTPUT_DIR + File.separator + "downloads";
    public static final String TEST_DATA_DIR =
            USER_DIR + File.separator + "src" + File.separator + "test" + File.separator + "resources"
                    + File.separator + "testdata";
    public static final String CONFIG_FILE = "config.properties";
}
