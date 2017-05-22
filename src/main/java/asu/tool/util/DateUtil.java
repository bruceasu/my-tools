/*
 * Copyright © 2016 Victor.su<victor.su@gwtsz.net>
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * “Software”), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package asu.tool.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.nutz.json.Json;
import org.nutz.lang.Strings;

/**
 * 日期类工具
 *
 * @version 1.0.0
 */
public class DateUtil
{
    public final static String CHINA_STSANDARD_DATE_FORMAT = "yyyy-MM-dd";

    public final static String CHINA_STSANDARD_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public final static String CHINA_OLD_STSANDARD_DATE_FORMAT = "yyyy.MM.dd";

    public final static String CHINA_OLD_STSANDARD_DATE_TIME_FORMAT = "yyyy.MM.dd HH:mm:ss";

    public final static String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    /**
     * MM/dd/yyyy
     */
    public static final SimpleDateFormat US_DATE_FORMATOR = new SimpleDateFormat("MM/dd/yyyy");
    /**
     * MM/dd/yyyy HH:mm
     */
    public static final SimpleDateFormat US_DATE_TIME_FORMATOR = new SimpleDateFormat("MM/dd/yyyy HH:mm");
    /**
     * MM/dd/yyyy HH:mm:ss
     */
    public static final SimpleDateFormat DATE_TIME_EXTENDED_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    /**
     * yyyyMMdd
     */
    public static final SimpleDateFormat ORA_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    /**
     * yyyyMMddHHmm
     */
    public static final SimpleDateFormat ORA_DATE_TIME_FORMAT = new SimpleDateFormat("yyyyMMddHHmm");
    /**
     * yyyyMMddHHmmss
     */
    @SuppressWarnings("unused")
    public static final SimpleDateFormat ORA_DATE_TIME_EXTENDED_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");

    public static final int[] dayArray = new int[]{31, 28, 31, 30, 31, 30,
            31, 31, 30, 31, 30, 31};

//	private static SimpleDateFormat sdf = new SimpleDateFormat();

    public static synchronized Calendar getCalendar()
    {
        return GregorianCalendar.getInstance();
    }

    /**
     * 功能：获取当前日期对应的yyyy-MM-dd HH:mm:ss,SSS字符串
     *
     * @return String
     */
    public static synchronized String getDateMilliFormat()
    {
        Calendar cal = Calendar.getInstance();
        return getDateMilliFormat(cal);
    }

    /**
     * 功能：日期转换成yyyy-MM-dd HH:mm:ss,SSS字符串
     *
     * @param cal date
     * @return String
     */
    public static synchronized String getDateMilliFormat(Calendar cal)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss,SSS";
        return getDateFormat(cal, pattern);
    }

    /**
     * 功能：日期转换成yyyy-MM-dd HH:mm:ss,SSS字符串
     *
     * @param date Date
     * @return String
     */
    public static synchronized String getDateMilliFormat(Date date)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss,SSS";
        return getDateFormat(date, pattern);
    }

    /**
     * 功能：字符串转换成yyyy-MM-dd HH:mm:ss,SSS日期
     *
     * @param strDate date
     * @return java.util.Calendar
     */
    public static synchronized Calendar parseCalendarMilliFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss,SSS";
        return parseCalendarFormat(strDate, pattern);
    }

    /**
     * 功能：字符串转换成yyyy-MM-dd HH:mm:ss,SSS日期
     *
     * @param strDate date
     * @return java.util.Date
     */
    public static synchronized Date parseDateMilliFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss,SSS";
        return parseDateFormat(strDate, pattern);
    }

    /**
     * 功能：获取当前日期对应的yyyy-MM-dd HH:mm:ss字符串
     *
     * @return String
     */
    public static synchronized String getDateSecondFormat()
    {
        Calendar cal = Calendar.getInstance();
        return getDateSecondFormat(cal);
    }

    /**
     * 功能：日期转换成yyyy-MM-dd HH:mm:ss字符串
     *
     * @param cal date
     * @return String
     */
    public static synchronized String getDateSecondFormat(Calendar cal)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return getDateFormat(cal, pattern);
    }

    /**
     * 功能：日期转换成yyyy-MM-dd HH:mm:ss字符串
     *
     * @param date date
     * @return String
     */
    public static synchronized String getDateSecondFormat(Date date)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return getDateFormat(date, pattern);
    }

    /**
     * 功能：字符串转换成yyyy-MM-dd HH:mm:ss日期
     *
     * @param strDate date
     * @return java.util.Calendar
     */
    public static synchronized Calendar parseCalendarSecondFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return parseCalendarFormat(strDate, pattern);
    }

    /**
     * 功能：字符串转换成yyyy-MM-dd HH:mm:ss日期
     *
     * @param strDate date
     * @return java.util.Date
     */
    public static synchronized Date parseDateSecondFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return parseDateFormat(strDate, pattern);
    }

    /**
     * 功能：字符串转换成yyyy/MM/dd HH:mm:ss日期
     *
     * @param strDate date
     * @return java.util.Date
     */
    public static synchronized Date parseDateSecond2Format(String strDate)
    {
        if (strDate != null && strDate.indexOf("-") != -1) {
            strDate = strDate.replaceAll("-", "/");
        }
        String pattern = "yyyy/MM/dd HH:mm:ss";
        return parseDateFormat(strDate, pattern);
    }

    /**
     * @return String
     */
    public static synchronized String getDateMinuteFormat()
    {
        Calendar cal = Calendar.getInstance();
        return getDateMinuteFormat(cal);
    }

    /**
     * 功能：获取日期前一天的日期
     *
     * @param date date
     * @return java.util.Date
     */
    public static synchronized Date getPreviousDay(Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        gc.add(Calendar.DATE, -1);
        return gc.getTime();
    }

    /**
     * 功能：获取当年的字符串
     *
     * @return java.util.Date
     */
    public static synchronized String getYear()
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        String year = Integer.toString(gc.get(Calendar.YEAR));
        return year;
    }

    /**
     * @param cal date
     * @return String
     */
    public static synchronized String getDateMinuteFormat(Calendar cal)
    {
        String pattern = "yyyy-MM-dd HH:mm";
        return getDateFormat(cal, pattern);
    }

    /**
     * 功能：日期转换成yyyy-MM-dd HH:mm字符串
     *
     * @param date date
     * @return String
     */
    public static synchronized String getDateMinuteFormat(Date date)
    {
        String pattern = "yyyy-MM-dd HH:mm";
        return getDateFormat(date, pattern);
    }

    /**
     * 功能：字符串转换成yyyy-MM-dd HH:mm日期
     *
     * @param strDate date
     * @return java.util.Calendar
     */
    public static synchronized Calendar parseCalendarMinuteFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd HH:mm";
        return parseCalendarFormat(strDate, pattern);
    }

    /**
     * 功能：字符串转换成yyyy-MM-dd HH:mm日期
     *
     * @param strDate date
     * @return java.util.Date
     */
    public static synchronized Date parseDateMinuteFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd HH:mm";
        return parseDateFormat(strDate, pattern);
    }

    /**
     * 功能：获取当天yyyy-MM-dd字符串
     *
     * @return String
     */
    public static synchronized String getDateDayFormat()
    {
        Calendar cal = Calendar.getInstance();
        return getDateDayFormat(cal);
    }

    /**
     * 功能：获取前一天yyyy-MM-dd字符串
     *
     * @return String
     */
    public static synchronized String getDatePreDayFormat()
    {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return getDateDayFormat(cal);
    }

    /**
     * 功能：日期转换成yyyy-MM-dd字符串
     *
     * @param cal date
     * @return String
     */
    public static synchronized String getDateDayFormat(Calendar cal)
    {
        String pattern = "yyyy-MM-dd";
        return getDateFormat(cal, pattern);
    }

    /**
     * 功能：日期转换成yyyy-MM-dd字符串
     *
     * @param date date
     * @return String
     */
    public static synchronized String getDateDayFormat(Date date)
    {
        String pattern = "yyyy-MM-dd";
        return getDateFormat(date, pattern);
    }

    /**
     * 功能：字符串转换成yyyy-MM-dd日期
     *
     * @param strDate date
     * @return java.util.Calendar
     */
    public static synchronized Calendar parseCalendarDayFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd";
        return parseCalendarFormat(strDate, pattern);
    }

    /**
     * 功能：字符串转换成yyyy-MM-dd日期
     *
     * @param strDate date
     * @return java.util.Date
     */
    public static synchronized Date parseDateDayFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd";
        return parseDateFormat(strDate, pattern);
    }

    /**
     * 功能：字符串转换成yyyy-MM日期
     *
     * @param strDate date
     * @return java.util.Calendar
     */
    public static synchronized Calendar parseCalendarMonthFormat(String strDate)
    {
        String pattern = "yyyy-MM";
        return parseCalendarFormat(strDate, pattern);
    }

    /**
     * 功能：字符串转换成yyyy-MM日期
     *
     * @param strDate date
     * @return java.util.Date
     */
    public static synchronized Date parseDateMonthFormat(String strDate)
    {
        String pattern = "yyyy-MM";
        return parseDateFormat(strDate, pattern);
    }

    /**
     * 功能：日期转换成yyyy-MM字符串
     *
     * @param cal date
     * @return String
     */
    public static synchronized String getDateMonthFormat(Calendar cal)
    {
        String pattern = "yyyy-MM";
        return getDateFormat(cal, pattern);
    }

    /**
     * 功能：日期转换成yyyy-MM字符串
     *
     * @param date date
     * @return String
     */
    public static synchronized String getDateMonthFormat(Date date)
    {
        String pattern = "yyyy-MM";
        return getDateFormat(date, pattern);
    }

    /**
     * 功能：获取当天yyyy-MM字符串
     *
     * @return String yyyy-MM字符串
     */
    public static synchronized String getDateMonthFormat()
    {
        Calendar cal = Calendar.getInstance();
        return getDateMonthFormat(cal);
    }

    /**
     * 功能：获取当天yyyy-MM-dd_HH-mm-ss字符串
     *
     * @return String
     */
    public static synchronized String getDateFileFormat()
    {
        Calendar cal = Calendar.getInstance();
        return getDateFileFormat(cal);
    }

    /**
     * 功能：日期转换成yyyy-MM-dd_HH-mm-ss字符串
     *
     * @param cal date
     * @return String
     */
    public static synchronized String getDateFileFormat(Calendar cal)
    {
        String pattern = "yyyy-MM-dd_HH-mm-ss";
        return getDateFormat(cal, pattern);
    }

    /**
     * 功能：日期转换成yyyy-MM-dd_HH-mm-ss字符串
     *
     * @param date date
     * @return String
     */
    public static synchronized String getDateFileFormat(Date date)
    {
        String pattern = "yyyy-MM-dd_HH-mm-ss";
        return getDateFormat(date, pattern);
    }

    /**
     * 功能：字符串转换成yyyy-MM-dd_HH-mm-ss日期
     *
     * @param strDate date
     * @return java.util.Calendar
     */
    public static synchronized Calendar parseCalendarFileFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd_HH-mm-ss";
        return parseCalendarFormat(strDate, pattern);
    }

    /**
     * 功能：字符串转换成yyyy-MM-dd_HH-mm-ss日期
     *
     * @param strDate date
     * @return java.util.Date
     */
    public static synchronized Date parseDateFileFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd_HH-mm-ss";
        return parseDateFormat(strDate, pattern);
    }

    /**
     * @return String
     */
    public static synchronized String getDateW3CFormat()
    {
        Calendar cal = Calendar.getInstance();
        return getDateW3CFormat(cal);
    }

    /**
     * @param cal date
     * @return String
     */
    public static synchronized String getDateW3CFormat(Calendar cal)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return getDateFormat(cal, pattern);
    }

    /**
     * @param date date
     * @return String
     */
    public static synchronized String getDateW3CFormat(Date date)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return getDateFormat(date, pattern);
    }

    /**
     * @param strDate date
     * @return java.util.Calendar
     */
    public static synchronized Calendar parseCalendarW3CFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return parseCalendarFormat(strDate, pattern);
    }

    /**
     * @param strDate date
     * @return java.util.Date
     */
    public static synchronized Date parseDateW3CFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return parseDateFormat(strDate, pattern);
    }

    /**
     * @param cal date
     * @return String
     */
    public static synchronized String getDateFormat(Calendar cal)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return getDateFormat(cal, pattern);
    }

    /**
     * @param date date
     * @return String
     */
    public static synchronized String getDateFormat(Date date)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return getDateFormat(date, pattern);
    }

    /**
     * @param strDate date
     * @return java.util.Calendar
     */
    public static synchronized Calendar parseCalendarFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return parseCalendarFormat(strDate, pattern);
    }

    /**
     * @param strDate date
     * @return java.util.Date
     */
    public static synchronized Date parseDateFormat(String strDate)
    {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        return parseDateFormat(strDate, pattern);
    }

    /**
     * @param cal     date
     * @param pattern pattern
     * @return String
     */
    public static synchronized String getDateFormat(Calendar cal,
                                                    String pattern)
    {
        return getDateFormat(cal.getTime(), pattern);
    }

    /**
     * 功能：获取yyyyMMddHHmmss格式字符串
     *
     * @return String
     */
    public final static String toYyyymmddHhmmss()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    /**
     * 功能：获取今天日期（yyyyMMdd），以中国北京时间为准
     *
     * @return String
     */
    public final static String today()
    {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date);
    }

    /**
     * 功能：获取yyyyMMdd格式字符串
     *
     * @return String
     */
    public final static String toYyyymmdd()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return sdf.format(new Date());
    }

    /**
     * 功能：获取yyyyMMdd格式字符串
     *
     * @return String
     */
    public final static String toYyyymm()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
        return sdf.format(new Date());
    }

    /**
     * @param date    date
     * @param pattern pattern
     * @return String
     */
    public static synchronized String getDateFormat(Date date,
                                                    String pattern)
    {
        SimpleDateFormat sdf = new SimpleDateFormat();
        String str = null;
        sdf.applyPattern(pattern);
        str = sdf.format(date);
        return str;
    }

    /**
     * 功能：将毫秒转换为yyyy-MM-dd HH:mm:ss日期
     *
     * @param longMs time
     * @return String
     */
    public static synchronized String longMsTimeConvertToDateTime(long longMs)
    {
        Date dat = new Date(longMs);
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(dat);
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return formatter.format(gc.getTime());
    }

    /**
     * @param strDate date
     * @param pattern pattern
     * @return java.util.Calendar
     */
    public static synchronized Calendar parseCalendarFormat(String strDate,
                                                            String pattern)
    {
        SimpleDateFormat sdf = new SimpleDateFormat();
        Calendar cal = null;
        sdf.applyPattern(pattern);
        try {
            sdf.parse(strDate);
            cal = sdf.getCalendar();
        } catch (Exception e) {
        }
        return cal;
    }

    /**
     * @param strDate date
     * @param pattern pattern
     * @return java.util.Date
     */
    public static synchronized Date parseDateFormat(String strDate,
                                                    String pattern)
    {
        if (Strings.isBlank(strDate)) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat();
        Date date = null;
        sdf.applyPattern(pattern);
        try {
            date = sdf.parse(strDate);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
        return date;
    }

    public static synchronized int getLastDayOfMonth(int month)
    {
        if (month < 1 || month > 12) {
            return -1;
        }
        int retn = 0;
        if (month == 2) {
            if (isLeapYear()) {
                retn = 29;
            } else {
                retn = dayArray[month - 1];
            }
        } else {
            retn = dayArray[month - 1];
        }
        return retn;
    }

    public static synchronized int getLastDayOfMonth(int year, int month)
    {
        if (month < 1 || month > 12) {
            return -1;
        }
        int retn = 0;
        if (month == 2) {
            if (isLeapYear(year)) {
                retn = 29;
            } else {
                retn = dayArray[month - 1];
            }
        } else {
            retn = dayArray[month - 1];
        }
        return retn;
    }

    public static synchronized int getDayOfWeek(Date date)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static synchronized boolean isLeapYear()
    {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        return isLeapYear(year);
    }

    public static synchronized boolean isLeapYear(int year)
    {
        if ((year % 400) == 0)
            return true;
        else if ((year % 4) == 0) {
            if ((year % 100) == 0)
                return false;
            else
                return true;
        } else
            return false;
    }

    public static synchronized boolean isLeapYear(Date date)
    {
        // int year = date.getYear();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        int year = gc.get(Calendar.YEAR);
        return isLeapYear(year);
    }

    public static synchronized boolean isLeapYear(Calendar gc)
    {
        int year = gc.get(Calendar.YEAR);
        return isLeapYear(year);
    }

    public static synchronized Date getPreviousWeekDay(
            Date date)
    {

        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return getPreviousWeekDay(gc);

    }

    public static synchronized Date getPreviousWeekDay(
            Calendar gc)
    {
        switch (gc.get(Calendar.DAY_OF_WEEK)) {
            case (Calendar.MONDAY):
                gc.add(Calendar.DATE, -3);
                break;
            case (Calendar.SUNDAY):
                gc.add(Calendar.DATE, -2);
                break;
            default:
                gc.add(Calendar.DATE, -1);
                break;
        }
        return gc.getTime();
    }

    public static synchronized Date getNextWeekDay(Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        switch (gc.get(Calendar.DAY_OF_WEEK)) {
            case (Calendar.FRIDAY):
                gc.add(Calendar.DATE, 3);
                break;
            case (Calendar.SATURDAY):
                gc.add(Calendar.DATE, 2);
                break;
            default:
                gc.add(Calendar.DATE, 1);
                break;
        }
        return gc.getTime();
    }

    public static synchronized Calendar getNextWeekDay(
            Calendar gc)
    {
        switch (gc.get(Calendar.DAY_OF_WEEK)) {
            case (Calendar.FRIDAY):
                gc.add(Calendar.DATE, 3);
                break;
            case (Calendar.SATURDAY):
                gc.add(Calendar.DATE, 2);
                break;
            default:
                gc.add(Calendar.DATE, 1);
                break;
        }
        return gc;
    }

    public static synchronized boolean isTodaySaturday(Date today)
    {
        Calendar gc = Calendar.getInstance();
        gc.setTime(today);
        if (gc.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return true;
        }
        return false;
    }

    public static synchronized boolean isTodaySaturday()
    {
        Calendar gc = Calendar.getInstance();
        if (gc.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            return true;
        }
        return false;
    }

    public static synchronized Date getLastDayOfNextMonth(
            Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        gc.setTime(DateUtil.getNextMonth(gc.getTime()));
        gc.setTime(DateUtil.getLastDayOfMonth(gc.getTime()));
        return gc.getTime();
    }

    public static synchronized Date getLastDayOfNextWeek(
            Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        gc.setTime(DateUtil.getNextWeek(gc.getTime()));
        gc.setTime(DateUtil.getLastDayOfWeek(gc.getTime()));
        return gc.getTime();
    }

    public static synchronized Date getFirstDayOfNextMonth(
            Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        gc.setTime(DateUtil.getNextMonth(gc.getTime()));
        gc.setTime(DateUtil.getFirstDayOfMonth(gc.getTime()));
        return gc.getTime();
    }

    public static synchronized Calendar getFirstDayOfNextMonth(
            Calendar gc)
    {
        gc.setTime(DateUtil.getNextMonth(gc.getTime()));
        gc.setTime(DateUtil.getFirstDayOfMonth(gc.getTime()));
        return gc;
    }

    public static synchronized Date getFirstDayOfNextWeek(
            Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        gc.setTime(DateUtil.getNextWeek(gc.getTime()));
        gc.setTime(DateUtil.getFirstDayOfWeek(gc.getTime()));
        return gc.getTime();
    }

    public static synchronized Calendar getFirstDayOfNextWeek(
            Calendar gc)
    {
        gc.setTime(DateUtil.getNextWeek(gc.getTime()));
        gc.setTime(DateUtil.getFirstDayOfWeek(gc.getTime()));
        return gc;
    }

    /**
     * ȡ��ָ�����ڵ���һ����
     *
     * @param date ָ�����ڡ�
     * @return ָ�����ڵ���һ����
     */
    public static synchronized Date getLastMonth(Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        gc.add(Calendar.MONTH, -1);
        return gc.getTime();
    }

    public static synchronized Date getLastMonth(Date date,
                                                 int n)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        gc.add(Calendar.MONTH, -n);
        return gc.getTime();
    }

    public static synchronized Calendar getLastMonth(
            Calendar gc)
    {
        gc.add(Calendar.MONTH, -1);
        return gc;
    }

    public static synchronized Date getNextMonth(Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        gc.add(Calendar.MONTH, 1);
        return gc.getTime();
    }

    public static synchronized Calendar getNextMonth(
            Calendar gc)
    {
        gc.add(Calendar.MONTH, 1);
        return gc;
    }

    public static synchronized Date getNextDay(Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        gc.add(Calendar.DATE, 1);
        return gc.getTime();
    }

    public static synchronized Calendar getNextDay(
            Calendar gc)
    {
        gc.add(Calendar.DATE, 1);
        return gc;
    }

    /**
     * ȡ��ָ�����ڵ���һ������
     *
     * @param date ָ�����ڡ�
     * @return ָ�����ڵ���һ������
     */
    public static synchronized Date getPreviousWeek(
            Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        gc.add(Calendar.DATE, -7);
        return gc.getTime();
    }

    public static synchronized Calendar getPreviousWeek(
            Calendar gc)
    {
        gc.add(Calendar.DATE, -7);
        return gc;
    }

    public static synchronized Date getNextWeek(Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        gc.add(Calendar.DATE, 7);
        return gc.getTime();
    }

    public static synchronized Calendar getNextWeek(
            Calendar gc)
    {
        gc.add(Calendar.DATE, 7);
        return gc;
    }

    public static synchronized Date getLastDayOfWeek(
            Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        switch (gc.get(Calendar.DAY_OF_WEEK)) {
            case (Calendar.SUNDAY):
                gc.add(Calendar.DATE, 6);
                break;
            case (Calendar.MONDAY):
                gc.add(Calendar.DATE, 5);
                break;
            case (Calendar.TUESDAY):
                gc.add(Calendar.DATE, 4);
                break;
            case (Calendar.WEDNESDAY):
                gc.add(Calendar.DATE, 3);
                break;
            case (Calendar.THURSDAY):
                gc.add(Calendar.DATE, 2);
                break;
            case (Calendar.FRIDAY):
                gc.add(Calendar.DATE, 1);
                break;
            case (Calendar.SATURDAY):
                gc.add(Calendar.DATE, 0);
                break;
        }
        return gc.getTime();
    }

    public static synchronized Date getFirstDayOfWeek(
            Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        switch (gc.get(Calendar.DAY_OF_WEEK)) {
            case (Calendar.SUNDAY):
                gc.add(Calendar.DATE, 0);
                break;
            case (Calendar.MONDAY):
                gc.add(Calendar.DATE, -1);
                break;
            case (Calendar.TUESDAY):
                gc.add(Calendar.DATE, -2);
                break;
            case (Calendar.WEDNESDAY):
                gc.add(Calendar.DATE, -3);
                break;
            case (Calendar.THURSDAY):
                gc.add(Calendar.DATE, -4);
                break;
            case (Calendar.FRIDAY):
                gc.add(Calendar.DATE, -5);
                break;
            case (Calendar.SATURDAY):
                gc.add(Calendar.DATE, -6);
                break;
        }
        return gc.getTime();
    }

    public static synchronized Calendar getFirstDayOfWeek(
            Calendar gc)
    {
        switch (gc.get(Calendar.DAY_OF_WEEK)) {
            case (Calendar.SUNDAY):
                gc.add(Calendar.DATE, 0);
                break;
            case (Calendar.MONDAY):
                gc.add(Calendar.DATE, -1);
                break;
            case (Calendar.TUESDAY):
                gc.add(Calendar.DATE, -2);
                break;
            case (Calendar.WEDNESDAY):
                gc.add(Calendar.DATE, -3);
                break;
            case (Calendar.THURSDAY):
                gc.add(Calendar.DATE, -4);
                break;
            case (Calendar.FRIDAY):
                gc.add(Calendar.DATE, -5);
                break;
            case (Calendar.SATURDAY):
                gc.add(Calendar.DATE, -6);
                break;
        }
        return gc;
    }

    public static synchronized Date getLastDayOfMonth(
            Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        switch (gc.get(Calendar.MONTH)) {
            case 0:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 1:
                gc.set(Calendar.DAY_OF_MONTH, 28);
                break;
            case 2:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 3:
                gc.set(Calendar.DAY_OF_MONTH, 30);
                break;
            case 4:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 5:
                gc.set(Calendar.DAY_OF_MONTH, 30);
                break;
            case 6:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 7:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 8:
                gc.set(Calendar.DAY_OF_MONTH, 30);
                break;
            case 9:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 10:
                gc.set(Calendar.DAY_OF_MONTH, 30);
                break;
            case 11:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
        }

        if ((gc.get(Calendar.MONTH) == Calendar.FEBRUARY)
                && (isLeapYear(gc.get(Calendar.YEAR))))
        {
            gc.set(Calendar.DAY_OF_MONTH, 29);
        }
        return gc.getTime();
    }

    public static synchronized Calendar getLastDayOfMonth(
            Calendar gc)
    {
        switch (gc.get(Calendar.MONTH)) {
            case 0:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 1:
                gc.set(Calendar.DAY_OF_MONTH, 28);
                break;
            case 2:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 3:
                gc.set(Calendar.DAY_OF_MONTH, 30);
                break;
            case 4:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 5:
                gc.set(Calendar.DAY_OF_MONTH, 30);
                break;
            case 6:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 7:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 8:
                gc.set(Calendar.DAY_OF_MONTH, 30);
                break;
            case 9:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
            case 10:
                gc.set(Calendar.DAY_OF_MONTH, 30);
                break;
            case 11:
                gc.set(Calendar.DAY_OF_MONTH, 31);
                break;
        }
        if ((gc.get(Calendar.MONTH) == Calendar.FEBRUARY)
                && (isLeapYear(gc.get(Calendar.YEAR))))
        {
            gc.set(Calendar.DAY_OF_MONTH, 29);
        }
        return gc;
    }

    public static synchronized Date getFirstDayOfMonth(
            Date date)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        gc.set(Calendar.DAY_OF_MONTH, 1);
        return gc.getTime();
    }

    public static synchronized Calendar getFirstDayOfMonth(
            Calendar gc)
    {
        gc.set(Calendar.DAY_OF_MONTH, 1);
        return gc;
    }

    public static synchronized Date getPreviousNDay(
            Date date, int n)
    {
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        gc.add(Calendar.DATE, n);
        return gc.getTime();
    }

    public static synchronized String toString(Date theDate,
                                               DateFormat theDateFormat)
    {
        if (theDate == null)
            return "";
        return theDateFormat.format(theDate);
    }

    public static String getDateByMillTime(long millSeconds)
    {
        Calendar gc = Calendar.getInstance();
        gc.setTimeInMillis(millSeconds * 1000 - 8 * 3600 * 1000);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(gc.getTime());
    }


    public static Date getFirstDayOfThisMonth()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(calendar.get(GregorianCalendar.YEAR), calendar
                .get(GregorianCalendar.MONTH), calendar
                .get(GregorianCalendar.DATE), 0, 0, 0);
        return calendar.getTime();
    }

    public static Date getFirstDayOfNextMonth()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.add(GregorianCalendar.MONTH, 1);
        calendar.set(calendar.get(GregorianCalendar.YEAR), calendar
                .get(GregorianCalendar.MONTH), calendar
                .get(GregorianCalendar.DATE), 0, 0, 0);
        return calendar.getTime();
    }

    public static Date getYesterday()
    {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        calendar.set(calendar.get(GregorianCalendar.YEAR), calendar
                .get(GregorianCalendar.MONTH), calendar
                .get(GregorianCalendar.DATE), 0, 0, 0);
        return calendar.getTime();
    }

    public static Date getLastSecondOfToday()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(calendar.get(GregorianCalendar.YEAR), calendar
                .get(GregorianCalendar.MONTH), calendar
                .get(GregorianCalendar.DATE), 23, 59, 59);
        return calendar.getTime();
    }

    public static Date getMidDayOfThisMonthExptSunday()
    {
        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(GregorianCalendar.YEAR), calendar
                .get(GregorianCalendar.MONTH), 15, 0, 0, 0);
        int days = calendar.get(Calendar.DAY_OF_WEEK);
        if (days == 1) {
            calendar.add(Calendar.DATE, 1);
        }
        Date midMonth = calendar.getTime();
        Date clearingBeginDate = getFirstDayOfThisMonth();
        if (today.after(midMonth)) {
            clearingBeginDate = midMonth;
        }
        return clearingBeginDate;
    }

    public static Date getMaxDate()
    {
        Calendar calendar = new GregorianCalendar();
        calendar.set(2999, 1, 1, 0, 0, 0);
        return calendar.getTime();
    }

    public static Timestamp now()
    {
        Calendar currDate = Calendar.getInstance();
        return new Timestamp((currDate.getTime()).getTime());
    }

    public static String getTimePast(long beginAt)
    {
        long completeAt = System.currentTimeMillis();
        long interval = completeAt - beginAt;
        long second = interval / 1000;
        long minute = 0;
        long hour = 0;
        String timeStr = second + "s";
        if (second >= 60) {
            minute = second / 60;
            second = second % 60;
            timeStr = minute + "m " + second + "s";
        }
        if (minute >= 60) {
            hour = minute / 60;
            minute = minute % 60;
            timeStr = hour + "h " + minute + "m " + second + "s";
        }

        return timeStr;
    }

    public static Date getLastSatOfThisMonth()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 3);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal.add(Calendar.DAY_OF_YEAR, -1);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        while (dayOfWeek != Calendar.SATURDAY) {
            cal.add(Calendar.DAY_OF_YEAR, -1);
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        }

        return cal.getTime();
    }

    //Time A
    public static Date get3DayBeforeLastSatOfThisMonth()
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getLastSatOfThisMonth());
        cal.add(Calendar.DAY_OF_YEAR, -3);
        return cal.getTime();
    }

    public static Date getLastSatOfLastMonth()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 3);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal.add(Calendar.DAY_OF_YEAR, -1);
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

        while (dayOfWeek != Calendar.SATURDAY) {
            cal.add(Calendar.DAY_OF_YEAR, -1);
            dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        }

        return cal.getTime();
    }

    public static Double roundDouble(double val, int precision)
    {
        Double ret = null;
        try {
            double factor = Math.pow(10, precision);
            ret = Math.floor(val * factor + 0.5) / factor;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public final static String toYyMmdd(Date aDate)
    {
        if (aDate == null)
            return "";
        Calendar cal = new GregorianCalendar();
        cal.setTime(aDate);
        StringBuilder sb = new StringBuilder();
        int nYear = cal.get(Calendar.YEAR);
        nYear = nYear % 100;
        int nMonth = cal.get(Calendar.MONTH);
        nMonth++;
        int nDay = cal.get(Calendar.DAY_OF_MONTH);
        if (nYear < 10)
            sb.append('0');
        sb.append(nYear);
        if (nMonth < 10)
            sb.append('0');
        sb.append(nMonth);
        if (nDay < 10)
            sb.append('0');
        sb.append(nDay);
        return sb.toString();
    }

    public final static String toYyyymmddHhmmss(Date aDate)
    {
        if (aDate == null)
            return "";
        Calendar cal = new GregorianCalendar();
        cal.setTime(aDate);
        int nYear = cal.get(Calendar.YEAR);
        int nMonth = cal.get(Calendar.MONTH);
        nMonth++;
        int nDay = cal.get(Calendar.DAY_OF_MONTH);
        int nHour = cal.get(Calendar.HOUR_OF_DAY);
        int nMInute = cal.get(Calendar.MINUTE);
        int nSeconf = cal.get(Calendar.SECOND);

        StringBuilder sb = new StringBuilder();
        sb.append(nYear);
        sb.append('-');
        if (nMonth < 10)
            sb.append('0');
        sb.append(nMonth);
        sb.append('-');
        if (nDay < 10)
            sb.append('0');
        sb.append(nDay);

        sb.append(' ');

        if (nHour < 10)
            sb.append('0');
        sb.append(nHour);
        sb.append(':');
        if (nMInute < 10)
            sb.append('0');
        sb.append(nMInute);
        sb.append(':');
        if (nSeconf < 10)
            sb.append('0');
        sb.append(nSeconf);

        return sb.toString();
    }

    /**
     * 功能: 获取当前时间的秒数
     *
     * @return 当前时间的秒数
     */
    public static String time()
    {
        return String.valueOf(System.currentTimeMillis() / 1000);
    }

    /**
     * 功能：比较两个时间的大小
     *
     * @param date1 date1
     * @param date2 date2
     * @return int
     */
    public static int compareToDate(String date1, String date2)
    {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(date1);
            Date dt2 = df.parse(date2);
            if (dt1.getTime() > dt2.getTime()) {
                return 0;
            } else if (dt1.getTime() < dt2.getTime()) {
                return 1;
            } else {
                return 2;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            return 3;
        }
    }

    /**
     * 格式dateWeekTime 日期
     *
     * @param dateWeekTime date
     * @return String
     */
    public static String formatDateWeekTime(String dateWeekTime)
    {
        String[] weekStrArr = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        StringBuffer loc_result = new StringBuffer();
        if (Strings.isBlank(dateWeekTime)) {
            return "";
        } else {
            dateWeekTime = dateWeekTime.trim();
            HashMap dateWeekTimeJSON = Json.fromJson(HashMap.class, dateWeekTime);
            String datestartTmp = (String) dateWeekTimeJSON.get("beginDate");
            if (Strings.isNotBlank(datestartTmp)) {
                loc_result.append(datestartTmp.replaceAll("-", "."));
            }
            String dateEndTmp = (String) dateWeekTimeJSON.get("endDate");
            if (Strings.isNotBlank(dateEndTmp) && dateEndTmp.equals(datestartTmp) == false) {
                loc_result.append("-");
                loc_result.append(dateEndTmp.replaceAll("-", "."));
            }
            if (dateWeekTimeJSON.containsKey("weekTime")) {
                Object[] timeArrTmp = (Object[]) dateWeekTimeJSON.get("weekTime");
                if (timeArrTmp != null) {
                    for (Object o : timeArrTmp) {
                        Map timpTmp = null;
                        String timeStartTmp = null;
                        String timeWeekTmp = null;
                        String timeEndTmp = null;
                        timpTmp = (Map) o;

                        loc_result.append(" ");
                        timeWeekTmp = (String) timpTmp.get("week");
                        if (Strings.isNotBlank(timeWeekTmp)) {
                            loc_result.append("[");
                            loc_result.append(weekStrArr[Integer.parseInt(timeWeekTmp)]);
                            loc_result.append("]");
                        }
                        timeStartTmp = (String) timpTmp.get("beginTime");
                        if (Strings.isNotBlank(timeStartTmp)) {
                            loc_result.append(timeStartTmp);
                        }
                        timeEndTmp = (String) timpTmp.get("endTime");
                        if (Strings.isNotBlank(timeEndTmp) && timeEndTmp.equals(timeStartTmp) == false) {
                            loc_result.append("-");
                            loc_result.append(timeEndTmp);
                        }
                    }
                }
            }
        }
        return loc_result.toString();
    }

    ;

    /**
     * 功能：将日期转换为指定格式的字符串
     *
     * @param date    date
     * @param pattern pattern
     * @return String
     */
    public static String formatDate(Date date, String pattern)
    {
        SimpleDateFormat sf = new SimpleDateFormat();
        sf.applyPattern(pattern);
        return sf.format(date);
    }

    /**
     * 检查当前日期是否符合日期插件数据
     *
     * @param dateWeekTime date
     * @param nullResult   空值结果
     *                     1）对于禁言设置，空值表示没有设置禁言，即当前时间不包含在其中。传值false
     *                     2）对于聊天规则设置，空值表示永久生效，即当前时间包含在其中。传值true
     * @return true or false
     */
    public static boolean dateTimeWeekCheck(String dateWeekTime, boolean nullResult)
    {
        if (Strings.isBlank(dateWeekTime)) {
            return nullResult;
        }
        dateWeekTime = dateWeekTime.trim();
        HashMap dateWeekTimeJSON = (HashMap) Json.fromJson(HashMap.class, dateWeekTime);
        String dateStartTmp = (String) dateWeekTimeJSON.get("beginDate");
        String dateEndTmp = (String) dateWeekTimeJSON.get("endDate");
        Calendar currCalendar = Calendar.getInstance();
        boolean isPass = false;
        String currDateStr = DateUtil.getDateDayFormat(currCalendar);
        isPass = Strings.isBlank(dateStartTmp) || currDateStr.compareTo(dateStartTmp) >= 0;
        if (isPass) {
            isPass = Strings.isBlank(dateEndTmp) || currDateStr.compareTo(dateEndTmp) <= 0;
        }

        //日期校验通过，则校验时间
        if (isPass && dateWeekTimeJSON.containsKey("weekTime")) {
            Object[] timeArrTmp = (Object[]) dateWeekTimeJSON.get("weekTime");
            if (timeArrTmp == null || timeArrTmp.length == 0) {
                return isPass;
            }
            boolean weekTimePass = false;
            for (Object o : timeArrTmp) {
                HashMap timpTmp = (HashMap) o;
                String timeStartTmp = null;
                String timeWeekTmp = null;
                String timeEndTmp = null;
                String currWeekStr = String.valueOf(currCalendar.get(Calendar.DAY_OF_WEEK) - 1);
                String currTimeStr = DateUtil.getDateFormat(currCalendar, "HH:mm:ss");
                timeWeekTmp = (String) timpTmp.get("week");
                if (Strings.isNotBlank(timeWeekTmp) && timeWeekTmp.equals(currWeekStr) == false) {
                    continue;
                }
                timeStartTmp = (String) timpTmp.get("beginTime");
                timeEndTmp = (String) timpTmp.get("endTime");
                weekTimePass = (Strings.isBlank(timeStartTmp) || currTimeStr.compareTo(timeStartTmp) >= 0);
                if (weekTimePass) {
                    weekTimePass = Strings.isBlank(timeEndTmp) || currTimeStr.compareTo(timeEndTmp) <= 0;
                }
                if (weekTimePass) {
                    break;
                }
            }
            return weekTimePass;
        }
        return isPass;
    }

    ;

    /**
     * 按照指定的周期获取起始时间
     *
     * @param date            date
     * @param cycle：H、D、W、M、Y
     * @return date
     */
    public static Date getStartDateOfCycle(Date date, String cycle)
    {
        Calendar loc_calendar = Calendar.getInstance();
        loc_calendar.setTime(date);
        if ("H".equals(cycle)) {
            //小时
            loc_calendar.set(Calendar.MINUTE, 0);
            loc_calendar.set(Calendar.SECOND, 0);
            loc_calendar.set(Calendar.MILLISECOND, 0);
        } else if ("D".equals(cycle)) {
            //天
            loc_calendar.set(Calendar.HOUR_OF_DAY, 0);
            loc_calendar.set(Calendar.MINUTE, 0);
            loc_calendar.set(Calendar.SECOND, 0);
            loc_calendar.set(Calendar.MILLISECOND, 0);
        } else if ("W".equals(cycle)) {
            //周：注意这是以星期日作为每周的起始日
            loc_calendar.set(Calendar.DAY_OF_WEEK, 1);
            loc_calendar.set(Calendar.HOUR_OF_DAY, 0);
            loc_calendar.set(Calendar.MINUTE, 0);
            loc_calendar.set(Calendar.SECOND, 0);
            loc_calendar.set(Calendar.MILLISECOND, 0);
        } else if ("M".equals(cycle)) {
            //月
            loc_calendar.set(Calendar.DAY_OF_MONTH, 1);
            loc_calendar.set(Calendar.HOUR_OF_DAY, 0);
            loc_calendar.set(Calendar.MINUTE, 0);
            loc_calendar.set(Calendar.SECOND, 0);
            loc_calendar.set(Calendar.MILLISECOND, 0);
        } else if ("Y".equals(cycle)) {
            //年
            loc_calendar.set(Calendar.MONTH, 0);
            loc_calendar.set(Calendar.DAY_OF_MONTH, 1);
            loc_calendar.set(Calendar.HOUR_OF_DAY, 0);
            loc_calendar.set(Calendar.MINUTE, 0);
            loc_calendar.set(Calendar.SECOND, 0);
            loc_calendar.set(Calendar.MILLISECOND, 0);
        }
        return loc_calendar.getTime();
    }

    /**
     * 按照指定的周期获取起始时间
     *
     * @param date            date
     * @param cycle：H、D、W、M、Y
     * @return date
     */
    public static Date getEndDateOfCycle(Date date, String cycle)
    {
        Calendar loc_calendar = Calendar.getInstance();
        loc_calendar.setTime(date);
        if ("H".equals(cycle)) {
            //小时
            loc_calendar.set(Calendar.MINUTE, 59);
            loc_calendar.set(Calendar.SECOND, 59);
            loc_calendar.set(Calendar.MILLISECOND, 999);
        } else if ("D".equals(cycle)) {
            //天
            loc_calendar.set(Calendar.HOUR_OF_DAY, 23);
            loc_calendar.set(Calendar.MINUTE, 59);
            loc_calendar.set(Calendar.SECOND, 59);
            loc_calendar.set(Calendar.MILLISECOND, 999);
        } else if ("W".equals(cycle)) {
            //周：注意这是以星期日作为每周的起始日
            loc_calendar.set(Calendar.DAY_OF_WEEK, 7);
            loc_calendar.set(Calendar.HOUR_OF_DAY, 23);
            loc_calendar.set(Calendar.MINUTE, 59);
            loc_calendar.set(Calendar.SECOND, 59);
            loc_calendar.set(Calendar.MILLISECOND, 999);
        } else if ("M".equals(cycle)) {
            //月 月份+1-
            loc_calendar.add(Calendar.MONTH, 1);
            loc_calendar.set(Calendar.DAY_OF_MONTH, 0);
            loc_calendar.set(Calendar.HOUR_OF_DAY, 23);
            loc_calendar.set(Calendar.MINUTE, 59);
            loc_calendar.set(Calendar.SECOND, 59);
            loc_calendar.set(Calendar.MILLISECOND, 999);
        } else if ("Y".equals(cycle)) {
            //年
            loc_calendar.set(Calendar.MONTH, 11);
            loc_calendar.set(Calendar.DAY_OF_MONTH, 31);
            loc_calendar.set(Calendar.HOUR_OF_DAY, 23);
            loc_calendar.set(Calendar.MINUTE, 59);
            loc_calendar.set(Calendar.SECOND, 59);
            loc_calendar.set(Calendar.MILLISECOND, 999);
        }
        return loc_calendar.getTime();
    }

    public static void main(String[] args)
    {
        String str = getDateSecondFormat(Calendar.getInstance().getTime());
        System.out.println(str);

        System.out.println(DateUtil.today());

        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        Date date = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateString = dateFormat.format(date);
        System.out.println(dateString);
    }
}
