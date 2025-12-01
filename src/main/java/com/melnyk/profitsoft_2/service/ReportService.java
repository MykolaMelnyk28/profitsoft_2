package com.melnyk.profitsoft_2.service;

import java.io.IOException;
import java.io.OutputStream;

public interface ReportService<T> {

    void write(Iterable<T> iterable, OutputStream out) throws IOException;

}
