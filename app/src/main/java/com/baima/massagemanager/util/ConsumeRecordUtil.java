package com.baima.massagemanager.util;

import com.baima.massagemanager.entity.ConsumeRecord;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ConsumeRecordUtil {

    /**
     * 消费记录按消费时间倒序排列
     *
     * @param consumeRecordList
     * @return
     */
    public static List<ConsumeRecord> sortConsumeTimestampDesc(List<ConsumeRecord> consumeRecordList) {
        Collections.sort(consumeRecordList, new Comparator<ConsumeRecord>() {
            @Override
            public int compare(ConsumeRecord o1, ConsumeRecord o2) {
                return Long.compare(o2.getConsumeTimestamp(), o1.getConsumeTimestamp());
            }
        });

        return consumeRecordList;
    }

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
                                .order("id desc").find(ConsumeRecord.class);
        return sortConsumeTimestampDesc(consumeRecordList);
    }

    /**
     * 根据指定时间戳指定员工获取前一天的消费记录
     * @param timeStamp
     * @param staffId
     * @return
     */
    public static List<ConsumeRecord> getConsumeRecordListPreviousDay(long timeStamp, long staffId) {
        List<ConsumeRecord> consumeRecordList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timeStamp);
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long timeInMillis = calendar.getTimeInMillis();
        consumeRecordList = LitePal.where("consumeTimestamp >= ? and consumeTimestamp < ? and staffId=?", String.valueOf(timeInMillis), String.valueOf(timeStamp), String.valueOf(staffId))
                .order("id desc").find(ConsumeRecord.class);
        return sortConsumeTimestampDesc(consumeRecordList);
    }
}
