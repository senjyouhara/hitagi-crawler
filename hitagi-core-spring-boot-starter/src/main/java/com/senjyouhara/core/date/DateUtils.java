package com.senjyouhara.core.date;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DateUtils {

    public static final String BASE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String BASE_DATE_FORMAT_BY_DAY = "yyyy-MM-dd";

    public static final String BASE_DATE_FORMAT_BY_MONTH = "yyyy-MM";

    public static final String BASE_DATE_FORMAT_BY_YEAR = "yyyy";

    public static final String timeZone = "GMT+8";

    /**
     * LocalDateTime转date
     * @param localDateTime
     * @return
     */
    public static Date localDateToDate(LocalDateTime localDateTime){
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    /**
     * date转LocalDateTime
     * @param date
     * @return
     */
    public static LocalDateTime dateToLocalDate(Date date){

        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();

        return instant.atZone(zoneId).toLocalDateTime();
    }

    /**
     * 时间戳转LocalDateTime
     * @param timestamp
     * @return
     */
    public static LocalDateTime timestampToLocalDatetime(long timestamp){
        return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     *  LocalDateTime转时间戳
     * @param ldt
     * @return
     */
    public static long datetimeToTimestamp(LocalDateTime ldt){
        long timestamp = ldt.toInstant(ZoneOffset.of("+8")).toEpochMilli();
        return timestamp;
    }
    public static String dateFormatByMonth(){
        return LocalDate.now().format(DateTimeFormatter.ofPattern(BASE_DATE_FORMAT_BY_MONTH));
    }

    public static String dateFormat(){
        return dateFormat(BASE_DATE_FORMAT);
    }

    public static String dateFormat(String pattern){
       return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String dateFormat(LocalDateTime localDate,String pattern){
        return localDate.format(DateTimeFormatter.ofPattern(pattern));
    }


    public static LocalDateTime parseDate(String stringDate){
        return parseDate(stringDate, BASE_DATE_FORMAT);
    }

    public static LocalDateTime parseDate(String stringDate,String pattern){
        DateTimeFormatter df = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.parse(stringDate,df);
    }

    public static LocalDateTime getFirstDay(){
        LocalDateTime date = LocalDateTime.now();
        return getFirstDay(date);
    }

    public static LocalDateTime getFirstDay(LocalDateTime date){
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    public static LocalDateTime getLastDay(){
        LocalDateTime date = LocalDateTime.now();
        return  getLastDay(date);
    }

    public static LocalDateTime getLastDay(LocalDateTime date){
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    public static List<LocalDateTime> getFirstDayAndLastDay(){
        LocalDateTime date = LocalDateTime.now();
        return getFirstDayAndLastDay(date);
    }

    public static List<LocalDateTime> getFirstDayAndLastDay(LocalDateTime date){
        LocalDateTime firstDay = getFirstDay(date);
        LocalDateTime lastDay = getLastDay(date);
        List<LocalDateTime> localDateTimes = new ArrayList<>();
        localDateTimes.add(firstDay);
        localDateTimes.add(lastDay);
        return localDateTimes;
    }
}
