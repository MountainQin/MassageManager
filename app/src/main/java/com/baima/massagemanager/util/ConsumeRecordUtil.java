package com.baima.massagemanager.util;

import com.baima.massagemanager.entity.ConsumeRecord;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConsumeRecordUtil {

    /**
     * 获取 指定时间往前一天的集合
     *
     * @param timeStamp
     * @return
     */
    public static List<ConsumeRecord> getConsumeRecordListPreviousDay(long timeStamp) {
        return getConsumeRecordListPreviousDay(timeStamp, 1);
    }

    /**
     * 获取 指定时间指定天数的集合
     *
     * @param timeStamp
     * @param dayCount
     * @return
     */
    public static List<ConsumeRecord> getConsumeRecordListPreviousDay(long timeStamp, int dayCount) {
        List<ConsumeRecord> consumeRecordList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        calendar.add(Calendar.DAY_OF_MONTH, -dayCount);
        long timeInMillis = calendar.getTimeInMillis();
        consumeRecordList = LitePal.where("consumeTimestamp >= ? and consumeTimestamp < ?", String.valueOf(timeInMillis), String.valueOf(timeStamp))
                .order("consumeTimestamp desc").find(ConsumeRecord.class);
        return consumeRecordList;
    }
}
