package com.waben.option.common.util;

import com.google.common.collect.Lists;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.chrono.ChronoZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class TimeUtil {

    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 判断美国时间是否为夏令
     *
     * <p>
     * 美国的夏令时从3月的第二个周日开始到11月的第一个周日结束。
     * </p>
     *
     * @return 是否为夏令
     */
    public static boolean isUSSummerTime() {
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.MONTH, 2);
        startCal.set(Calendar.WEEK_OF_MONTH, 3);
        startCal.set(Calendar.DAY_OF_WEEK, 1);
        startCal.set(Calendar.HOUR_OF_DAY, 13);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.MONTH, 10);
        endCal.set(Calendar.WEEK_OF_MONTH, 2);
        endCal.set(Calendar.DAY_OF_WEEK, 1);
        endCal.set(Calendar.HOUR_OF_DAY, 13);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        Date now = new Date();
        return now.getTime() > startCal.getTime().getTime() && now.getTime() < endCal.getTime().getTime();
    }

    /**
     * 判断欧洲时间是否为夏令
     *
     * <p>
     * 欧洲的夏令时从3月的最后一个星期天到10月的最后一个星期天结束。
     * </p>
     *
     * @return 是否为夏令
     */
    public static boolean isEUSummerTime() {
        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.MONTH, 2);
        startCal.set(Calendar.WEEK_OF_MONTH, 5);
        startCal.set(Calendar.DAY_OF_WEEK, 1);
        startCal.set(Calendar.HOUR_OF_DAY, 13);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.MONTH, 9);
        endCal.set(Calendar.WEEK_OF_MONTH, 5);
        endCal.set(Calendar.DAY_OF_WEEK, 1);
        endCal.set(Calendar.HOUR_OF_DAY, 13);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        endCal.set(Calendar.MILLISECOND, 0);

        Date now = new Date();
        return now.getTime() > startCal.getTime().getTime() && now.getTime() < endCal.getTime().getTime();
    }

    public static long getTimeMillis(LocalDateTime time) {
        return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static long getTimeMillis(LocalDate date) {
        return getTimeMillis(LocalDateTime.of(date, LocalTime.of(0, 0)));
    }

    public static LocalDateTime getDateTime(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
    }

    public static LocalDateTime getDateTime(Long time) {
        if (time != null) {
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(time), ZoneId.systemDefault());
        }
        return null;
    }

    public static String getStringSecondTime(Long time) {
        if (time != null) {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(time);
        }
        return null;
    }

    public static String getStringHour(LocalDateTime time) {
        if (time != null) {
            return time.format(DateTimeFormatter.ofPattern("HH"));
        }
        return null;
    }

    public static String getStringDayTime(Long time) {
        if (time != null) {
            return new SimpleDateFormat("yyyy-MM-dd").format(time);
        }
        return null;
    }

    public static boolean isSaveSecond(long time1, long time2) {
        return new Long(time1 / 1000L).equals(new Long(time2 / 1000L));
    }

    public final static List<Integer> defaultWeekDayList = Lists.newArrayList(1, 2, 3, 4, 5);

    /**
     * 获取当天开始时间
     *
     * @return
     */
    public static Date getTodayStartTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        return todayStart.getTime();
    }

    /**
     * 获取当天结束时间
     *
     * @return
     */
    public static Date getTodayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        return todayEnd.getTime();
    }

    /**
     * 获取当前时间24小时前的时间
     *
     * @return
     */
    public static Date pastDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 24);
        return calendar.getTime();
    }

    /**
     * 获取当前时间一周前的时间
     *
     * @return
     */
    public static Date pastWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -7);
        return calendar.getTime();
    }

    /**
     * 获取当前时间一个月前的时间
     *
     * @return
     */
    public static Date pastMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }

    /**
     * 获取本周开始时间
     *
     * @return
     */
    public static Date getBeginDayOfWeek() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int dayofweek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayofweek == 1) {
            dayofweek += 7;
        }
        cal.add(Calendar.DATE, 2 - dayofweek);
        return getDayStartTime(cal.getTime());
    }

    /**
     * 获取本周的结束时间
     *
     * @return
     */
    public static Date getEndDayOfWeek() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getBeginDayOfWeek());
        cal.add(Calendar.DAY_OF_WEEK, 6);
        Date weekEndSta = cal.getTime();
        return getDayEndTime(weekEndSta);
    }

    /**
     * 获取本月的开始时间
     *
     * @return
     */
    public static Date getBeginDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        return getDayStartTime(calendar.getTime());
    }

    /**
     * 获取本月的结束时间
     *
     * @return
     */
    public static Date getEndDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(getNowYear(), getNowMonth() - 1, 1);
        int day = calendar.getActualMaximum(5);
        calendar.set(getNowYear(), getNowMonth() - 1, day);
        return getDayEndTime(calendar.getTime());
    }

    /**
     * 获取某个日期的开始时间
     *
     * @param d
     * @return
     */
    public static Timestamp getDayStartTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return new Timestamp(calendar.getTimeInMillis());
    }

    /**
     * 获取某个日期的结束时间
     *
     * @param d
     * @return
     */
    public static Timestamp getDayEndTime(Date d) {
        Calendar calendar = Calendar.getInstance();
        if (null != d) calendar.setTime(d);
        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return new Timestamp(calendar.getTimeInMillis());
    }

    //获取今年是哪一年
    public static Integer getNowYear() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return Integer.valueOf(gc.get(1));
    }

    //获取本月是哪一月
    public static int getNowMonth() {
        Date date = new Date();
        GregorianCalendar gc = (GregorianCalendar) Calendar.getInstance();
        gc.setTime(date);
        return gc.get(2) + 1;
    }

    public static String getStartDayOfWeekNo(int year,int weekNo){
        Calendar cal = getCalendarFormYear(year);
        cal.set(Calendar.WEEK_OF_YEAR, weekNo);
        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" +
                cal.get(Calendar.DAY_OF_MONTH);

    }

    /**
     * get the end day of given week no of a year.
     * @param year
     * @param weekNo
     * @return
     */
    public static String getEndDayOfWeekNo(int year,int weekNo){
        Calendar cal = getCalendarFormYear(year);
        cal.set(Calendar.WEEK_OF_YEAR, weekNo);
        cal.add(Calendar.DAY_OF_WEEK, 6);
        return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH) + 1) + "-" +
                cal.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * get Calendar of given year
     * @param year
     * @return
     */
    private static Calendar getCalendarFormYear(int year){
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        cal.set(Calendar.YEAR, year);
        return cal;
    }

    public static String LocaDateToDate(LocalDate localDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        ZoneId zoneId = ZoneId.systemDefault();
        ChronoZonedDateTime<LocalDate> zonedDateTime = localDate.atStartOfDay(zoneId);
        return sdf.format(Date.from(zonedDateTime.toInstant()));
    }

}
