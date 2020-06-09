package com.baima.massagemanager.util;

import android.widget.DatePicker;

import java.util.Calendar;

public class DatePickerUtil {
    /**
     * 根据指定时间戳更新日期选择器的时间
     *
     * @param datePicker
     * @param timeInMillis
     */
    public static void updateDate(DatePicker datePicker, long timeInMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeInMillis);
        datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 获取 指定日期选择器的时间戳，小时分钟秒毫秒清零
     *
     * @param datePicker
     * @return
     */
    public static long getTimeInMillis(DatePicker datePicker) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(
                datePicker.getYear(),
                datePicker.getMonth(),
                datePicker.getDayOfMonth(),
                0,0,0
        );
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTimeInMillis();

    }
}
