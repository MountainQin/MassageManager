package com.baima.massagemanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.WorkStaff;
import com.baima.massagemanager.util.StringUtil;

import org.litepal.LitePal;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StaffRecordAdapter extends RecyclerView.Adapter<StaffRecordAdapter.ViewHolder> {

    private Context context;
    public List<ConsumeRecord> consumeRecordList;
    private long staffId;

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

    public void setStaffId(long staffId) {
        this.staffId = staffId;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_staff_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final ConsumeRecord consumeRecord = consumeRecordList.get(position);
        long consumeTimestamp = consumeRecord.getConsumeTimestamp();
        holder.tv_time.setText(new Date(consumeTimestamp).toLocaleString());
        //如果 是1日，背景设置为蓝色
        holder.view.setBackground(null);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(consumeTimestamp);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        if (day == 1) {
            holder.view.setBackgroundColor(0x880000FF);
        }

        //设置员工相关数据
        //根据消费记录ID和员工ID到工作员工库找记录
        List<WorkStaff> workStaffList = LitePal.where("consumeRecordId=? and staffId=?", String.valueOf(consumeRecord.getId()), String.valueOf(staffId))
                .find(WorkStaff.class);
        if (workStaffList.size() > 0) {
            WorkStaff workStaff = workStaffList.get(0);
            double workTime = workStaff.getWorkTime();
            holder.tv_work_time.setText("工作：" + StringUtil.doubleTrans(workTime) + "小时");
            double currentMonthTime = workStaff.getCurrentMonthTime();
            holder.tv_month_time.setText("本月：" + StringUtil.doubleTrans(currentMonthTime) + "小时");
        }

        holder.tv_customer_name.setText("顾客:" + consumeRecord.getCustomeName());
        //如果 有多个工作员工才显示 员工姓名
        holder.tv_staff_names.setText("");
        if (LitePal.where("consumeRecordId=?",String.valueOf(consumeRecord.getId())).find(WorkStaff.class).size()>1){
            holder.tv_staff_names.setText("员工：" + consumeRecord.getStaffName());
        }
        holder.tv_remark.setText(consumeRecord.getRemark());
        long timestampFlag = consumeRecord.getTimestampFlag();
        holder.tv_timestamp_flag.setText(String.valueOf(timestampFlag));
        if (MainActivity.isShowTimestampFlag) {
            holder.tv_timestamp_flag.setVisibility(View.VISIBLE);
        } else {
            holder.tv_timestamp_flag.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return consumeRecordList.size();
    }
}
