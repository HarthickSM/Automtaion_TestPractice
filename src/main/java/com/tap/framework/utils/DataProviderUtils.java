package com.tap.framework.utils;

import java.util.List;
import java.util.Map;

/** Converts test data collections into the {@code Object[][]} shape TestNG data providers need. */
public final class DataProviderUtils {

    private DataProviderUtils() {
    }

    public static Object[][] toObjectArray(List<? extends Map<String, ?>> rows) {
        Object[][] data = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) {
            data[i][0] = rows.get(i);
        }
        return data;
    }
}
