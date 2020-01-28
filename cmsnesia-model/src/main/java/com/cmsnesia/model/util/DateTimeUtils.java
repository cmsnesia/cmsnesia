package com.cmsnesia.model.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {

  private static final String PATTERN = "dd-MM-yyyy hh:mm:ss";

  public static String toString(Date date) {
    DateFormat dateFormat = new SimpleDateFormat(PATTERN);
    return dateFormat.format(date);
  }

  public static Date toDate(String string) throws ParseException {
    DateFormat dateFormat = new SimpleDateFormat(PATTERN);
    return dateFormat.parse(string);
  }
}
