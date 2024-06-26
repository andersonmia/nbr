package com.nbr.bankingsystem.utils;

import lombok.Data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class DateUtil {

    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    // Converts a date string to Date
    public static Date toDate(String date) {
        try {
            return dateFormatter.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format. Please use yyyy-MM-dd");
        }
    }
}
