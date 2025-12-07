package com.melnyk.profitsoft_2.service;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Service interface for generating reports from a collection of data items.
 *
 * <p>This interface defines a generic contract for writing a sequence of elements
 * of type {@code T} into an output stream in a specific report format
 * (e.g., CSV, PDF, Excel, etc.). Implementations are responsible for defining
 * the exact output structure and formatting.</p>
 *
 * @param <T> the type of elements that will be written to the report
 */
public interface ReportService<T> {

    void write(Iterable<T> iterable, OutputStream out) throws IOException;

}
