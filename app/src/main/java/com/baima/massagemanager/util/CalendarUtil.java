package com.baima.massagemanager.util;

import java.util.Calendar;

public class CalendarUtil {

    /**
     * 把指定日历对象的小时分钟秒毫秒清零
     *
     * @param calendar
     * @return
     */
    public static void setTimeTo0(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
