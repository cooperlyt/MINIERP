package com.dgsoft.common;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Synchronized;
import org.jboss.seam.annotations.intercept.BypassInterceptors;

import javax.faces.event.ValueChangeEvent;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/4/13
 * Time: 3:27 PM
 */

@Name("calendarBean")
@Scope(ScopeType.APPLICATION)
@Synchronized
@BypassInterceptors
public class CalendarBean implements java.io.Serializable {

    private Locale locale;
    private boolean popup;
    //private String pattern;
    private boolean showApply = false;
    private boolean useCustomDayLabels;
    private TimeZone timeZone;


    public CalendarBean() {

        locale = Locale.CHINA;
        timeZone = TimeZone.getTimeZone("GMT+8");
        popup = true;
        //pattern = this.getMsgs("calendarPattern");
    }

    public String getCurrencySymbol(){
       return Currency.getInstance(locale).getSymbol(locale);
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public boolean isPopup() {
        return popup;
    }

    public void setPopup(boolean popup) {
        this.popup = popup;
    }

    public void selectLocale(ValueChangeEvent event) {

        String tLocale = (String) event.getNewValue();
        if (tLocale != null) {
            String lang = tLocale.substring(0, 2);
            String country = tLocale.substring(3);
            locale = new Locale(lang, country, "");
        }
    }

    public boolean isUseCustomDayLabels() {
        return useCustomDayLabels;
    }

    public void setUseCustomDayLabels(boolean useCustomDayLabels) {
        this.useCustomDayLabels = useCustomDayLabels;
    }

    public boolean isShowApply() {
        return showApply;
    }

    public void setShowApply(boolean showApply) {
        this.showApply = showApply;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public static boolean isSameMonthDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSameWeekDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
                    .get(Calendar.WEEK_OF_YEAR)) {
                return true;
            }
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {

            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
                    .get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2
                    .get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }


    public static boolean isSameDayDate(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)) {
                if (cal1.get(Calendar.DAY_OF_MONTH) == cal2
                        .get(Calendar.DAY_OF_MONTH)) {
                    return true;
                }
            }
        }
        return false;
    }



}
