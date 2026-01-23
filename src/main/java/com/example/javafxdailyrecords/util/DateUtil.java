package com.example.javafxdailyrecords.util;

import java.time.LocalDate;

public class DateUtil {
    public static String getChineseWeek(LocalDate currentDay) {
        String week = "";
        switch (currentDay.getDayOfWeek()) {
            case MONDAY -> week = "星期一";
            case TUESDAY -> week = "星期二";
            case WEDNESDAY -> week = "星期三";
            case THURSDAY -> week = "星期四";
            case FRIDAY -> week = "星期五";
            case SATURDAY -> week = "星期六";
            case SUNDAY -> week = "星期天";
        }

        return week;
    }
}
