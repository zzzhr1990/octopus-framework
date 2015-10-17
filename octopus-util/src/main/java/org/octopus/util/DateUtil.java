package org.octopus.util;

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

/**
 * Date Utils
 * Created by zzzhr on 2015-10-17.
 */
@Slf4j
public class DateUtil {
    private static final DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    private static final DateTimeFormatter daytimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final DateTimeFormatter fullFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXXXX");

    private static final DateTimeFormatter RFC2822_Formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH);

    public static ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    public static void setDefaultZoneId(ZoneId defaultZoneId) {
        DEFAULT_ZONE_ID = defaultZoneId;
    }

    public static void setDefaultZoneId(String defaultZoneName) {
        DEFAULT_ZONE_ID = ZoneId.of(defaultZoneName);
    }

    public static Date getCustomDate(Long pttl) {
        return new Date(System.currentTimeMillis() + pttl);
    }

    public static Date getCustomDate(int day) {
        return Date.from(LocalDateTime.now().plusDays(day).atZone(DEFAULT_ZONE_ID).toInstant());
    }

    public static Date getCustomDay(int day) {
        return Date.from(LocalDate.now().plusDays(day).atStartOfDay(DEFAULT_ZONE_ID).toInstant());
    }

    public static Date getCustomDay(Date now, int day) {
        return Date.from(now.toInstant().plus(day, ChronoUnit.DAYS));
    }

    public static String getDay(Date time) {
        if (time == null) {
            time = new Date();
        }
        return dayFormatter.format(time.toInstant().atZone(DEFAULT_ZONE_ID));
    }

    public static String getTime(Date time) {
        if (time == null) {
            time = new Date();
        }
        return timeFormatter.format(time.toInstant().atZone(DEFAULT_ZONE_ID));
    }

    public static String getDayTime(Date time) {
        if (time == null) {
            time = new Date();
        }
        return daytimeFormatter.format(time.toInstant().atZone(DEFAULT_ZONE_ID));
    }

    public static String getRFC2822(Date time) {
        if (time == null) {
            time = new Date();
        }
        return RFC2822_Formatter.format(time.toInstant().atZone(DEFAULT_ZONE_ID));
    }

    public static String getPast(Date time) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime base = time.toInstant().atZone(DEFAULT_ZONE_ID);
        long hours = base.until(now, ChronoUnit.HOURS);
        if (hours >= 24) {
            return hours / 24 + "天前";
        } else if (hours < 1) {
            return "刚刚";
        } else {
            return hours + "小时前";
        }
    }

    public static String getFullDayTime(Date date) {
        if (date == null) {
            date = new Date();
        }
        return fullFormatter.format(date.toInstant().atZone(DEFAULT_ZONE_ID));
    }

    public static Date getDate(int year, int month) {
        try{
            return Date.from(LocalDate.of(year,month,1).atStartOfDay(DEFAULT_ZONE_ID).toInstant());
        }catch (Exception e){
            //LOGGER.error("error date",e);
            throw new RuntimeException("Invalidate Date",e);
        }
    }
}
