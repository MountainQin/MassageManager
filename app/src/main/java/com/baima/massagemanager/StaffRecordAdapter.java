package com.baima.massagemanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.util.StringUtil;

import java.util.Date;
import java.util.List;

public class StaffRecordAdapter extends RecyclerView.Adapter <StaffRecordAdapter.ViewHolder> {

    private Context context;
    private List<ConsumeRecord> consumeRecordList;

    class ViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tv_time;
        TextView tv_work_time;
        TextView tv_month_time;
        TextView tv_customer_name;
        TextView tv_staff_names;
        TextView tv_remark;
        TextView tv_timestamp_flag;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tv_time = itemView.findViewById(R.id.tv1);
            tv_work_time = itemView.findViewById(R.id.tv2);
            tv_month_time = itemView.findViewById(R.id.tv3);
            tv_customer_name = itemView.findViewById(R.id.tv4);
            tv_staff_names = itemView.findViewById(R.id.tv5);
            tv_remark = itemView.findViewById(R.id.tv6);
            tv_timestamp_flag = itemView.findViewById(R.id.tv7);
        }
    }

    public StaffRecordAdapter(Context context, List<ConsumeRecord> consumeRecordList) {
        this.context = context;
        this.consumeRecordList = consumeRecordList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_staff_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConsumeRecord consumeRecord = consumeRecordList.get(position);
        long consumeTimestamp = consumeRecord.getConsumeTimestamp();
        holder.tv_time.setText(new Date(consumeTimestamp).toLocaleString());
        double workTime = consumeRecord.getWorkTime();
        holder.tv_work_time.setText("工作："+StringUtil.doubleTrans(workTime)+"小时");
        double currentMonthTime = consumeRecord.getCurrentMonthTime();
        holder.tv_month_time.setText("本月："+StringUtil.doubleTrans(currentMonthTime)+"小时");
        holder.tv_customer_name.setText(consumeRecord.getCustomeName());
//        if (workTime!=consumeRecord.getConsumeTime()) {
            holder.tv_staff_names.setText(consumeRecord.getStaffName());
//        }
        holder.tv_remark.setText(consumeRecord.getRemark());
        long timestampFlag = consumeRecord.getTimestampFlag();
        holder.tv_timestamp_flag.setText(String.valueOf(timestampFlag));
    }

    @Override
    public int getItemCount() {
        return consumeRecordList.size();
    }
}
