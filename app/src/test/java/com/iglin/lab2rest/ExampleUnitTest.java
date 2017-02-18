package com.iglin.lab2rest;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void date() throws Exception {
        String str = "2009-11-21T22:03:00";

        //DateFormat formatter = SimpleDateFormat.getInstance();

        String format = "yyyy-MM-dd'T'HH:mm:ss";
        String shortFormat = "MMM d, h:mm a";
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.ENGLISH);


        SimpleDateFormat shortFormatter = new SimpleDateFormat(shortFormat, Locale.ENGLISH);
        Date date = formatter.parse(str);
        System.out.println(date);
        System.out.println(shortFormatter.format(date));
    }
}