package com.dgsoft.common.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/6/13
 * Time: 11:15 AM
 */
public class DataFormat {



    public static BigDecimal halfUpCurrency(BigDecimal number, Locale locale) {
        NumberFormat currencyFormat = DecimalFormat.getCurrencyInstance();
        try {
            return new BigDecimal(currencyFormat.parse(currencyFormat.format(number)).toString());
        } catch (ParseException e) {
            return number;
        }
    }

    public static BigDecimal halfUpCurrency(BigDecimal number) {
        return halfUpCurrency(number,Locale.CHINA);
    }



    public static BigDecimal format(BigDecimal value, String formatStr) {
        DecimalFormat df = new DecimalFormat(formatStr);
        df.setGroupingUsed(false);
        df.setRoundingMode(RoundingMode.HALF_UP);
        try {
            return new BigDecimal(df.parse(df.format(value)).toString());
        } catch (ParseException e) {
            return value;
        }

    }

    public static Date halfTime(Date value){
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(value);
        gc.set(Calendar.HOUR_OF_DAY, 0);
        gc.set(Calendar.MINUTE, 0);
        gc.set(Calendar.SECOND, 0);
        gc.set(Calendar.MILLISECOND, 0);
        return gc.getTime();
    }

    public static boolean isEmpty(String value){
        return (value == null) || (value.trim().equals(""));
    }

    public static boolean isEmpty(BigDecimal value) {
        return value != null && (value.compareTo(BigDecimal.ZERO) != 0);
    }

}
