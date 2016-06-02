package com.primefaces.ext.base.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Jason
 */
public class DateUitl {

    public static final String yyyyMMDD = "yyyy/MM/DD";
    public static final String MySql_DateTime = "yyyy-MM-dd HH:mm:ss";

    /**
     * to determine String whether is date format yyyy/MM/DD
     *
     * @param str String data
     * @return boolean
     */
    public static boolean isYyyyDDddDate(String str) {
        return isDate(str, "yyyy/MM/dd");
    }
    public static String getMySqlDateTime() {
        return customFormat(new Date(),MySql_DateTime);
    }
    
    public static boolean isYyyyDDddDate(String str,String format) {
        return isDate(str, format);
    }

    /**
     *
     * @param str input String
     * @param format date format
     * @return is true if can parse to date ,else false
     */
    public static boolean isDate(String str, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            simpleDateFormat.parse(str);
        } catch (ParseException ex) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param date java.util.Date
     * @param format date format: yyyy/MM/DD ………
     * @return String
     */
    public static String customFormat(Date date, String format) {
        if (date != null && format != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            try {
                return simpleDateFormat.format(date);
            } catch (Exception e) {
                //TODO: logger
                System.err.println("DateUitl: " + e.getLocalizedMessage());
                return null;
            }
        }
        return null;
    }
}