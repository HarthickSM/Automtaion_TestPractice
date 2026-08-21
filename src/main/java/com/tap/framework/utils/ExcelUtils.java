package com.tap.framework.utils;

import com.tap.framework.constants.FrameworkConstants;
import com.tap.framework.exceptions.FrameworkException;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/** Reads .xlsx test data as a list of column-name to value maps (data driven layer). */
public final class ExcelUtils {

    private ExcelUtils() {
    }

    public static List<Map<String, String>> read(String fileName, String sheetName) {
        File file = new File(FrameworkConstants.TEST_DATA_DIR, fileName);
        if (!file.exists()) {
            throw new FrameworkException("Excel test data file not found: " + file.getAbsolutePath());
        }
        List<Map<String, String>> rows = new ArrayList<>();
        try (FileInputStream stream = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(stream)) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet == null) {
                throw new FrameworkException("Sheet '" + sheetName + "' missing in " + fileName);
            }
            DataFormatter formatter = new DataFormatter();
            Row header = sheet.getRow(sheet.getFirstRowNum());
            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Map<String, String> record = new LinkedHashMap<>();
                for (int c = 0; c < header.getLastCellNum(); c++) {
                    Cell headerCell = header.getCell(c);
                    if (headerCell == null) {
                        continue;
                    }
                    record.put(formatter.formatCellValue(headerCell).trim(),
                            formatter.formatCellValue(row.getCell(c)).trim());
                }
                if (record.values().stream().anyMatch(value -> !value.isEmpty())) {
                    rows.add(record);
                }
            }
        } catch (FrameworkException e) {
            throw e;
        } catch (Exception e) {
            throw new FrameworkException("Unable to read " + fileName + " / " + sheetName, e);
        }
        return rows;
    }
}
