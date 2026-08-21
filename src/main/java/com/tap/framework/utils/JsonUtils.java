package com.tap.framework.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tap.framework.constants.FrameworkConstants;
import com.tap.framework.exceptions.FrameworkException;
import java.io.File;
import java.util.List;
import java.util.Map;

/** Reads JSON test data files from {@code src/test/resources/testdata}. */
public final class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtils() {
    }

    public static List<Map<String, Object>> readList(String fileName) {
        return read(fileName, new TypeReference<>() {
        });
    }

    public static Map<String, Object> readMap(String fileName) {
        return read(fileName, new TypeReference<>() {
        });
    }

    public static <T> T read(String fileName, TypeReference<T> type) {
        File file = new File(FrameworkConstants.TEST_DATA_DIR, fileName);
        try {
            return MAPPER.readValue(file, type);
        } catch (Exception e) {
            throw new FrameworkException("Unable to read JSON test data: " + file.getAbsolutePath(), e);
        }
    }
}
