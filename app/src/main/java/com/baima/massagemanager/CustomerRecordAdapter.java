package com.baima.massagemanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.RechargeRecord;
import com.baima.massagemanager.util.StringUtil;

import java.util.Date;
import java.util.List;

public class CustomerRecordAdapter extends RecyclerView.Adapter {

    private static final int TYPE_CONSUME_RECORD = 1;
    private static final int TYPE_RECHARGE_RECORD = 2;
    private Context context;
    private List consumeRechargeRecordList; //消费充值记录的集合

    public CustomerRecordAdapter(Context context, List consumeRechargeRecordList) {
        this.context = context;
        this.consumeRechargeRecordList = consumeRechargeRecordList;
    }


    //对应充值记录
    class RechargeRecordViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tv_recharge_time;
        TextView tv_recharge_amount;
        TextView tv_recharge_hour;
        TextView tv_remainder;
        TextView tv_remark;
        TextView tv_timestamp_flag;

        public RechargeRecordViewHolder(View view) {
            super(view);
            this.view = view;
            tv_recharge_time = view.findViewById(R.id.tv_recharge_time);
            tv_recharge_amount = view.findViewById(R.id.tv_recharge_amount);
            tv_recharge_hour = view.findViewById(R.id.tv_recharge_hour);
            tv_remainder = view.findViewById(R.id.tv_current_month_time);
            tv_remark = view.findViewById(R.id.tv_remark);
            tv_timestamp_flag = view.findViewById(R.id.tv_timestamp_flag);
        }
    }

    //对应消费记录
    class ConsumeRecordViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView tv_time;
        TextView tv_consume_time;
        TextView tv_remainder;
        TextView tv_staff;
        TextView tv_remark;
        TextView tv_timestamp_flag;

        public ConsumeRecordViewHolder(View view) {
            super(view);
            this.view = view;
            tv_time = view.findViewById(R.id.tv1);
            tv_consume_time = view.findViewById(R.id.tv2);
            tv_remainder = view.findViewById(R.id.tv3);
            tv_staff = view.findViewById(R.id.tv4);
            tv_remark = view.findViewById(R.id.tv5);
            tv_timestamp_flag = view.findViewById(R.id.tv6);
        }
    }

    @Override
    public int getItemViewType(int position) {
        //项目的类型
        Object o = consumeRechargeRecordList.get(position);
        if (o instanceof ConsumeRecord) {
            return TYPE_CONSUME_RECORD;
        } else if (o instanceof RechargeRecord) {
            return TYPE_RECHARGE_RECORD;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_CONSUME_RECORD) {
            //消费
            view = LayoutInflater.from(context).inflate(R.layout.item_consume_record, parent, false);
            return new ConsumeRecordViewHolder(view);
        } else {
            //充值
            view = LayoutInflater.from(context).inflate(R.layout.item_recharge_record, parent, false);
            return new RechargeRecordViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        Object o = consumeRechargeRecordList.get(position);
        if (o instanceof ConsumeRecord) {
            //消费
            ConsumeRecordViewHolder consumeRecordViewHolder = (ConsumeRecordViewHolder) holder;
            final ConsumeRecord consumeRecord = (ConsumeRecord) o;
            long consumeTimestamp = consumeRecord.getConsumeTimestamp();
            consumeRecordViewHolder.tv_time.setText(new Date(consumeTimestamp).toLocaleString());
            String consumeTimeStr = StringUtil.doubleTrans(consumeRecord.getConsumeTime());
            consumeRecordViewHolder.tv_consume_time.setText("消费：" + consumeTimeStr + "小时");
            String remainderStr = StringUtil.doubleTrans(consumeRecord.getRemainder());
            consumeRecordViewHolder.tv_remainder.setText("剩余：" + remainderStr + "小时");
            consumeRecordViewHolder.tv_staff.setText(consumeRecord.getStaffName());
            consumeRecordViewHolder.tv_remark.setText(consumeRecord.getRemark());
            consumeRecordViewHolder.tv_timestamp_flag.setText(String.valueOf(consumeRecord.getTimestampFlag()));
            if (MainActivity.isShowTimestampFlag){
                consumeRecordViewHolder.tv_timestamp_flag.setVisibility(View.VISIBLE);
            }else{
                consumeRecordViewHolder.tv_timestamp_flag.setVisibility(View.GONE);
            }
        } else if (o instanceof RechargeRecord) {
            //充值
            RechargeRecordViewHolder rechargeRecordViewHolder = (RechargeRecordViewHolder) holder;
            final RechargeRecord rechargeRecord = (RechargeRecord) o;
            long timeStamp = rechargeRecord.getTimeStamp();
            rechargeRecordViewHolder.tv_recharge_time.setText(new Date(timeStamp).toLocaleString());
            rechargeRecordViewHolder.tv_recharge_amount.setText("充值金额：" +
                    StringUtil.doubleTrans(rechargeRecord.getRechargeAmount(), true));
            rechargeRecordViewHolder.tv_recharge_hour.setText("充值：" +
                    StringUtil.doubleTrans(rechargeRecord.getRechargeHour()) + "小时");
            rechargeRecordViewHolder.tv_remainder.setText("剩余：" +
                    StringUtil.doubleTrans(rechargeRecord.getRemainder()) + "小时");
            rechargeRecordViewHolder.tv_remark.setText(rechargeRecord.getRemark());
            rechargeRecordViewHolder.tv_timestamp_flag.setText(String.valueOf(rechargeRecord.getTimestampFlag()));
            if (MainActivity.isShowTimestampFlag){
                rechargeRecordViewHolder.tv_timestamp_flag.setVisibility(View.VISIBLE);
            }else{
                rechargeRecordViewHolder.tv_timestamp_flag.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return consumeRechargeRecordList.size();
    }
}
