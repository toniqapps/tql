package com.toniq.tql.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateUtil
{
    public static final String NULL = "null";
    public static String LOG_FILE_NAME = "logfile.log";
    public static String DATE_FORMAT_SHORT = "dd/MM/yyyy cccc";
    public static String DATE_FORMAT_DATE_TIME = "dd-M-yyyy HH:mm";
    public static String DATE_FORMAT_DATE = "yyyy/MM/dd";
    public static String DATE_FORMAT_DATE_1 = "dd/MM/yyyy";
    public static String DATE_FORMAT_DATE_2 = "EEE, MMMM dd";
    public static String DATE_FORMAT_DAY_MONTH = "dd/MM";
    public static String DATE_FORMAT_TIME = "HH:mm:ss";
    public static String DATE_FORMAT_HOUR_MIN = "HH:mm";
    public static String DATE_FORMAT_FULL = "dd/MM/yyyy HH:mm:ss";

    public static Date ValueToDate(String strVal) throws ParseException
    {
        Date dtResultDate = null;
        if (strVal != null && !strVal.equals(StringUtil.EMPTY_STRING))
        {
            SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT_FULL, Locale.getDefault());
            dtResultDate = oSimpleDateFormat.parse(strVal);
        }
        return dtResultDate;
    }

    public static String DateToValue(Object oDateObject) throws ParseException
    {
        return getDate((Date) oDateObject, DATE_FORMAT_FULL);
    }

    public static long getTimeInMilli(int nYear, int nMonth, int nDay, int nHour, int nMin, int nSec)
    {
        Calendar oCalendar = Calendar.getInstance();
        oCalendar.set(nYear, nMonth, nDay, nHour, nMin, nSec);
        return oCalendar.getTimeInMillis();
    }

    public static long getTimeInMilli(int nHour, int nMin, int nSec)
    {
        Calendar oCalendar = Calendar.getInstance();
        oCalendar.set(1971, 0, 1, nHour, nMin, nSec);
        return oCalendar.getTimeInMillis();
    }

    public static int GetYear()
    {
        return Integer.parseInt(getDate(new Date(), "yyyy"));
    }

    public static int GetMonth()
    {
        return Integer.parseInt(getDate(new Date(), "MM")) - 1;
    }

    public static int GetDay()
    {
        return Integer.parseInt(getDate(new Date(), "dd"));
    }

    public static int getHour()
    {
        return Integer.parseInt(getDate(new Date(), "HH"));
    }

    public static int getMinute()
    {
        return Integer.parseInt(getDate(new Date(), "mm"));
    }

    public static String getDate(long lTime)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lTime);
        return getDate(calendar.getTime(), DATE_FORMAT_DATE);
    }

    public static String getTime(long lTime)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lTime);
        return getDate(calendar.getTime(), DATE_FORMAT_TIME);
    }

    public static String getTime(long lTime, String strFormat)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lTime);
        return getDate(calendar.getTime(), strFormat);
    }

    public static String getDate(Date oDate, String strDateFormat)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat, Locale.getDefault());
        return dateFormat.format(oDate);
    }

    public static String getDate(long lTime, String strDateFormat)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(lTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat(strDateFormat, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    public static boolean compareDates(String startDate, String endDate, String dateFormatDayMonth) throws Exception
    {
        SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat(dateFormatDayMonth, Locale.getDefault());
        return oSimpleDateFormat.parse(startDate).after(oSimpleDateFormat.parse(endDate));
    }

    public static boolean compareTimeWithCurrent(String strFrom) throws Exception
    {
        String strCurrentTime = getTime(System.currentTimeMillis() + 60000, DATE_FORMAT_HOUR_MIN);
        SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT_HOUR_MIN, Locale.getDefault());
        return oSimpleDateFormat.parse(strCurrentTime).after(oSimpleDateFormat.parse(strFrom));
    }

    public static boolean compareTimeWithCurrent(String strFrom, String strTo) throws Exception
    {
        String strCurrentTime = getTime(System.currentTimeMillis() + 60000, DATE_FORMAT_HOUR_MIN);
        SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT_HOUR_MIN, Locale.getDefault());
        return oSimpleDateFormat.parse(strCurrentTime).after(oSimpleDateFormat.parse(strFrom)) && oSimpleDateFormat.parse(strCurrentTime).before(oSimpleDateFormat.parse(strTo));
    }

    public static boolean compareTime(String strFrom, String strTo, String strTimeToCompare) throws Exception
    {
        SimpleDateFormat oSimpleDateFormat = new SimpleDateFormat(DATE_FORMAT_HOUR_MIN, Locale.getDefault());
        return oSimpleDateFormat.parse(strTimeToCompare).after(oSimpleDateFormat.parse(strFrom)) && oSimpleDateFormat.parse(strTimeToCompare).before(oSimpleDateFormat.parse(strTo));
    }

    public static long getTimeInMilli(String strDate, String strFormat) throws ParseException
    {
        SimpleDateFormat oFormat = new SimpleDateFormat(strFormat, Locale.getDefault());
        Date oDate = oFormat.parse(strDate);
        return oDate.getTime();
    }

    public static long getUTCTimeInMilli(long startTime)
    {
        return startTime + (new Date().getTimezoneOffset() * 1000 * 60);
    }
}
