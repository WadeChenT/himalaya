package com.otc.himalaya.utils;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class DateUtils {

    public static String getDate2GMTString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DateConstants.GMTFORMAT, Locale.ENGLISH);
        return sdf.format(date);
    }

    /**
     * 取得當前時間
     */
    public static LocalDateTime getNowLocal() {
        return LocalDateTime.now(ZoneId.systemDefault());
    }

    /**
     * 取得當前時間
     */
    public static Date getNowDate() {
        return Date.from(getNowLocal().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 取得當前時間字串
     */
    public static String getNowString() {
        return getNowLocal().format(DateConstants.YYYY_MM_DD_HH_mm_SS);
    }

    /**
     * 取得當前時間字串
     */
    public static String getNowStringMs() {
        return getNowLocal().format(DateConstants.YYYY_MM_DD_HH_mm_SS_MS);
    }


    /**
     * 取得當前時間字串
     */
    public static String getNowStringDmy() {
        return getNowLocal().format(DateConstants.DD_MM_YYYY);
    }
    public static Long countBusinessDaysBetween(LocalDate startDate, LocalDate endDate) {
        return countBusinessDaysBetween(startDate, endDate, Optional.of(new ArrayList<>()));
    }

    public static Long countBusinessDaysBetween(LocalDate startDate, LocalDate endDate, Optional<List<LocalDate>> holidays) {
        if (Objects.isNull(startDate) || Objects.isNull(endDate) || Objects.isNull(holidays)) {
            throw new IllegalArgumentException("Invalid method argument(s) to countBusinessDaysBetween(" + startDate
                                               + "," + endDate + "," + holidays + ")");
        }

        Predicate<LocalDate> isHoliday = date -> holidays.isPresent() ? holidays.get().contains(date) : false;

        Predicate<LocalDate> isWeekend = date -> date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY);

        Long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);

        Long businessDays = Stream.iterate(startDate, date -> date.plusDays(1)).limit(daysBetween)
                                  .filter(isHoliday.or(isWeekend).negate()).count();
        return businessDays;
    }


    public static long countDaysBetween(LocalDate startDate, LocalDate endDate) {
        if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
            throw new IllegalArgumentException("Invalid method argument(s) to countBusinessDaysBetween(" + startDate
                    + "," + endDate + ")");
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    public static LocalDate addBusinessDays(LocalDate localDate, int days){
        return addBusinessDays(localDate,days,Optional.empty());
    }

    public static LocalDate addBusinessDays(LocalDate localDate, int days,
                                             Optional<List<LocalDate>> holidays)
    {
        if(localDate == null || days <= 0 || holidays == null)
        {
            throw new IllegalArgumentException("Invalid method argument(s) "
                    + "to addBusinessDays("+localDate+","+days+","+holidays+")");
        }

        Predicate<LocalDate> isHoliday =
                date -> holidays.isPresent() ? holidays.get().contains(date) : false;

        Predicate<LocalDate> isWeekend = date
                -> date.getDayOfWeek() == DayOfWeek.SATURDAY
                || date.getDayOfWeek() == DayOfWeek.SUNDAY;

        LocalDate result = localDate;
        while (days > 0) {
            result = result.plusDays(1);
            if (isHoliday.or(isWeekend).negate().test(result)) {
                days--;
            }
        }
        return result;
    }

    public static class DateConstants {

        public static final String YMD = "yyyy-MM-dd";
        public static final String HMS = "HH:mm:ss";
        public static final String HM = "HH:mm";
        public static final String YMDHMS = "yyyy-MM-dd HH:mm:ss";
        public static final String YMDHMS2 = "yyyy/MM/dd HH:mm:ss";
        public static final String YMDHMSSSS = "yyyy-MM-dd HH:mm:ss.SSS";
        public static final String YMDHMSS = "yyyy-MM-dd HH:mm:ss.S";
        public static final String MDHM = "MM/dd HH:mm";
        public static final String DDMMYYYY = "dd/MM/yyyy";
        public static final String YMDwithoutSeparator = "yyyyMMddHHmmss";
        public static final String GMTFORMAT = "EEE, dd MMM YYYY HH:mm:ss 'GMT ('Z')'";
        /**
         * 日期格式化成yyyy-MM-dd
         */
        public static final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern(YMD);
        /**
         * 日期格式化成HH:mm:ss
         */
        public static final DateTimeFormatter HH_mm_SS = DateTimeFormatter.ofPattern(HMS);
        /**
         * 日期格式化成HH:mm
         */
        public static final DateTimeFormatter HH_mm = DateTimeFormatter.ofPattern(HM);
        /**
         * 日期格式化成yyyy-MM-dd HH:mm:ss
         */
        public static final DateTimeFormatter YYYY_MM_DD_HH_mm_SS = DateTimeFormatter.ofPattern(YMDHMS);
        /**
         * 日期格式化成yyyy/MM/dd HH:mm:ss，如2018/04/05 21:50:58
         */
        public static final DateTimeFormatter YYYY_MM_DD_HH_mm_SS2 = DateTimeFormatter.ofPattern(YMDHMS2);
        /**
         * 日期格式化成MM/dd HH:mm
         */
        public static final DateTimeFormatter MM_DD_HH_mm = DateTimeFormatter.ofPattern(MDHM);        /**
         * 日期格式化成dd/MM/yyyy
         */
        public static final DateTimeFormatter DD_MM_YYYY = DateTimeFormatter.ofPattern(DDMMYYYY);
        /**
         * 日期格式化成yyyy-MM-dd HH:mm:ss.SSS
         */
        public static final DateTimeFormatter YYYY_MM_DD_HH_mm_SS_MS = DateTimeFormatter.ofPattern(YMDHMSSSS);
        /**
         * 日期格式化成yyyy-MM-dd HH:mm:ss.S
         */
        public static final DateTimeFormatter YYYY_MM_DD_HH_mm_SS_S = DateTimeFormatter.ofPattern(YMDHMSS);
        /**
         * 日期格式化成yyyyMMddHHmmss
         */
        public static final DateTimeFormatter DATETIME_FORMAT_NO_SLASH = DateTimeFormatter.ofPattern(YMDwithoutSeparator);
        /**
         * 日期格式化成yyyyMMdd
         */
        public static final DateTimeFormatter DATE_FORMAT_NO_SLASH = DateTimeFormatter.ofPattern("yyyyMMdd");
        /**
         * 日期格式化成HHmmss
         */
        public static final DateTimeFormatter TIME_FORMAT_NO_SLASH = DateTimeFormatter.ofPattern("HHmmss");

    }
}
