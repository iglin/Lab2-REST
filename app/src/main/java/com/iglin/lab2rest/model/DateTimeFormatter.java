package com.iglin.lab2rest.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by user on 04.02.2017.
 */

public class DateTimeFormatter {
    private static String format = "yyyy-MM-dd'T'HH:mm:ss";
    private static String shortFormat = "MMM d, h:mm a";
    private static String fullFormat = "EEE, d MMM yyyy HH:mm";
    private static SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);
    private static SimpleDateFormat shortFormatter = new SimpleDateFormat(shortFormat, Locale.ENGLISH);
    private static SimpleDateFormat fullFormatter = new SimpleDateFormat(fullFormat, Locale.ENGLISH);

    public static Date getDate(String date) throws ParseException {
        return formatter.parse(date);
    }

    public static String getShortFormat(String date) throws ParseException {
         return shortFormatter.format(formatter.parse(date));
    }

    public static String getFullFormat(String date) throws ParseException {
        return fullFormatter.format(formatter.parse(date));
    }
}
