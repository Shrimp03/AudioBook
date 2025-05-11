package com.example.audiobook.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class FormatDate {
    public static String formatDateOfBirth(String date) {
        DateTimeFormatter inFmt  = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate ld = LocalDate.parse(date, inFmt);
        return ld.format(outFmt);
    }
}
