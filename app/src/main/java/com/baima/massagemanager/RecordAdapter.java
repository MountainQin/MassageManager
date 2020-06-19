package com.baima.massagemanager;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.util.StringUtil;

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
        holder.tv_work_time.setText(consumeRecord.getCustomeName());
        //消费时间
        double consumeTime = consumeRecord.getConsumeTime();
        holder.tv_month_time.setText("消费:" + StringUtil.doubleTrans(consumeTime) + "小时");

        holder.tv_staff_names.setText("员工:" + consumeRecord.getStaffName());
        holder.tv_remark.setText(consumeRecord.getRemark());
        holder.tv_timestamp_flag.setText(String.valueOf(consumeRecord.getTimestampFlag()));
        if (MainActivity.isShowTimestampFlag) {
            holder.tv_timestamp_flag.setVisibility(View.VISIBLE);
        } else {
            holder.tv_timestamp_flag.setVisibility(View.GONE);
        }
    }
}
