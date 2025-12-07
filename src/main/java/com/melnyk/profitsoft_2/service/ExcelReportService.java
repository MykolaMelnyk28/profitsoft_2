package com.melnyk.profitsoft_2.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract base class for generating Excel reports using Apache POI.
 *
 * <p>Implements {@link ReportService} and uses the Template Method pattern:
 * the report writing workflow is fixed, while subclasses define the sheet,
 * header structure, and how each item is written into a row.</p>
 *
 * @param <T> the type of exported items
 */
public abstract class ExcelReportService<T> implements ReportService<T> {

    @Override
    public final void write(Iterable<T> iterable, OutputStream out) throws IOException {
        Objects.requireNonNull(out, "OutputStream cannot be null");

        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {
            Map<String, CellType> headers = getHeaderNames();

            Sheet sheet = createSheet(workbook);
            Row headerRow = sheet.createRow(0);
            int cellNum = 0;
            for (var entry : headers.entrySet()) {
                Cell cell = headerRow.createCell(cellNum++);
                cell.setCellValue(entry.getKey());
            }

            int rowNum = 1;
            for (T item : iterable) {
                Row row = sheet.createRow(rowNum++);
                writeElementRow(item, row);
            }

            workbook.write(out);
        }
    }

    protected abstract Sheet createSheet(Workbook workbook);

    protected abstract Map<String, CellType> getHeaderNames();

    protected abstract void writeElementRow(T item, Row row);

    protected void writeStringCell(Row row, int cellNum, String value) {
        Cell cell = row.createCell(cellNum, CellType.STRING);
        if (value == null) cell.setBlank();
        else cell.setCellValue(value);
    }

    protected void writeNumericCell(Row row, int cellNum, Number value) {
        Cell cell = row.createCell(cellNum, CellType.NUMERIC);
        if (value == null) cell.setBlank();
        else cell.setCellValue(value.doubleValue());
    }

    protected void writeBlankCell(Row row, int cellNum) {
        row.createCell(cellNum, CellType.STRING).setBlank();
    }

}
