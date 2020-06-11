package com.baima.massagemanager;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.util.CalendarUtil;
import com.baima.massagemanager.util.StringUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RecordAdapter extends StaffRecordAdapter {

    public RecordAdapter(FragmentActivity activity, List<ConsumeRecord> consumeRecordList) {
        super(activity, consumeRecordList);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConsumeRecord consumeRecord = consumeRecordList.get(position);
        long consumeTimestamp = consumeRecord.getConsumeTimestamp();
        holder.tv_time.setText(new Date(consumeTimestamp).toLocaleString());
        //顾客 姓名
        long customerId = consumeRecord.getCustomerId();
        if (customerId > 0) {
            holder.tv_work_time.setText(consumeRecord.getCustomeName());
        } else if (customerId == -1) {
            //普通 顾客
            holder.tv_work_time.setText("普通 顾客 ：" + consumeRecord.getCustomeName());
        }
        //消费时间
        double consumeTime = consumeRecord.getConsumeTime();
        holder.tv_month_time.setText(StringUtil.doubleTrans(consumeTime) + "小时");

        holder.tv_staff_names.setText(consumeRecord.getStaffName());
        holder.tv_remark.setText(consumeRecord.getRemark());
        holder.tv_timestamp_flag.setText(String.valueOf(consumeRecord.getTimestampFlag()));
    }
}
