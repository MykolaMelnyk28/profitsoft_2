package com.melnyk.profitsoft_2.service.impl;

import com.melnyk.profitsoft_2.dto.response.BookInfoDto;
import com.melnyk.profitsoft_2.dto.response.GenreInfoDto;
import com.melnyk.profitsoft_2.entity.Book;
import com.melnyk.profitsoft_2.service.ExcelReportService;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Excel report for {@link Book} entities in {@link BookInfoDto} view.
 *
 * <p>Defines the sheet name, header columns, and how each item is written
 * into a row. Excel creation and streaming are handled by the base class.</p>
 */
@Service
public class BookExcelReportService extends ExcelReportService<BookInfoDto> {

    @Override
    protected Sheet createSheet(Workbook workbook) {
        return workbook.createSheet("books");
    }

    @Override
    protected Map<String, CellType> getHeaderNames() {
        Map<String, CellType> map = new LinkedHashMap<>();
        map.put("id", CellType.NUMERIC);
        map.put("title", CellType.STRING);
        map.put("description", CellType.STRING);
        map.put("author", CellType.STRING);
        map.put("yearPublished", CellType.NUMERIC);
        map.put("pages", CellType.NUMERIC);
        map.put("genres", CellType.STRING);
        return map;
    }

    @Override
    protected void writeElementRow(BookInfoDto book, Row row) {
        int cellNum = 0;
        writeNumericCell(row, cellNum++, book.getId());
        writeStringCell(row, cellNum++, book.getTitle());
        writeStringCell(row, cellNum++, book.getDescription());

        if (book.getAuthor() == null) {
            writeBlankCell(row, cellNum++);
        } else {
            String authorValue = book.getAuthor().getFirstName() + " " + book.getAuthor().getLastName();
            writeStringCell(row, cellNum++, authorValue);
        }

        writeNumericCell(row, cellNum++, book.getYearPublished());
        writeNumericCell(row, cellNum++, book.getPages());

        if (book.getGenres() == null || book.getGenres().isEmpty()) {
            writeBlankCell(row, cellNum++);
        } else {
            String genresValue = book.getGenres().stream()
                .map(GenreInfoDto::getName)
                .collect(Collectors.joining(","));
            writeStringCell(row, cellNum++, genresValue);
        }
    }

}
