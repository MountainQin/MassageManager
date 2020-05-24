package com.baima.massagemanager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.entity.RechargeRecord;
import com.baima.massagemanager.util.StringUtil;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CustomerRecordAdapter extends RecyclerView.Adapter {

    private Context context;
    private List<RechargeRecord> rechargeRecordList;

    public CustomerRecordAdapter(Context context, List<RechargeRecord> rechargeRecordList) {
        this.context = context;
        this.rechargeRecordList = rechargeRecordList;
    }

    class RechargeRecordViewHolder extends RecyclerView.ViewHolder {
        TextView tv_recharge_time;
        TextView tv_recharge_amount;
        TextView tv_recharge_hour;
        TextView tv_remainder;
        TextView tv_remark;

        public RechargeRecordViewHolder(View view) {
            super(view);
            tv_recharge_time = view.findViewById(R.id.tv_recharge_time);
            tv_recharge_amount = view.findViewById(R.id.tv_recharge_amount);
            tv_recharge_hour = view.findViewById(R.id.tv_recharge_hour);
            tv_remainder = view.findViewById(R.id.tv_remainder);
            tv_remark = view.findViewById(R.id.tv_remark);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_recharge_record, parent, false);
        RechargeRecordViewHolder rechargeRecordViewHolder = new RechargeRecordViewHolder(view);
        return rechargeRecordViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RechargeRecord rechargeRecord = rechargeRecordList.get(position);
        RechargeRecordViewHolder rechargeRecordViewHolder = (RechargeRecordViewHolder) holder;
        //把时间戳格式化
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分");
        Date date = new Date(rechargeRecord.getTimeStamp());
        String format = simpleDateFormat.format(date);
        rechargeRecordViewHolder.tv_recharge_time.setText(format);
        rechargeRecordViewHolder.tv_recharge_amount.setText("充值金额：" +
                StringUtil.doubleTrans(rechargeRecord.getRechargeAmount(), true));
        rechargeRecordViewHolder.tv_recharge_hour.setText("充值：" +
                StringUtil.doubleTrans(rechargeRecord.getRechargeHour()) + "小时");
        rechargeRecordViewHolder.tv_remainder.setText("剩余：" +
                StringUtil.doubleTrans(rechargeRecord.getRemainder()) + "小时");
        rechargeRecordViewHolder.tv_remark.setText(rechargeRecord.getRemark());
    }

    @Override
    public int getItemCount() {
        return rechargeRecordList.size();
    }
}
