package com.baima.massagemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baima.massagemanager.entity.Customer;
import com.baima.massagemanager.util.StringUtil;

import java.util.List;

public abstract class CustomerAdapter extends BaseAdapter {

    private Context context;
    private List<Customer> customerList;

    public CustomerAdapter(Context context, List<Customer> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

    /**
     * 列表项目里的充值点击 后回调
     *
     * @param customerId
     */
    public abstract void onRechargeClick(long customerId);

    /**
     * 列表项目里的消费标签点击 后回调
     *
     * @param customerId
     */
    public abstract void onConsumeClick(long customerId);

    @Override
    public int getCount() {
        return customerList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.item_customer, parent, false);
            holder.tv_number = convertView.findViewById(R.id.tv_number);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_remainder = convertView.findViewById(R.id.tv_month_time_later);
            holder.tv_recharge = convertView.findViewById(R.id.tv_recharge);
            holder.tv_consume = convertView.findViewById(R.id.tv_consume);
            holder.tv_remark = convertView.findViewById(R.id.tv_remark);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Customer customer = customerList.get(position);
        holder.tv_number.setText(String.valueOf(customer.getNumber()) + "号");
        holder.tv_name.setText(customer.getName());
        holder.tv_remainder.setText("剩余：" +
                StringUtil.doubleTrans(customer.getRemainder()) + "小时");
        holder.tv_remark.setText(customer.getRemark());

        //充值的点击 事件，打开充值活动界面
        holder.tv_recharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRechargeClick(customer.getId());
//        Fragment +ListView +adapter 项目里的组件 点击后，可以打开新活动，从新活动返回后不能回调onActivityForResult
                //要利用回调在Fragment打开新活动
//                ((Activity)context).startActivityForResult(intent, requestCode);
            }
        });


        holder.tv_consume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConsumeClick(customer.getId());
            }
        });
        return convertView;
    }

    static class ViewHolder {
        TextView tv_number;
        TextView tv_name;
        TextView tv_remainder;
        TextView tv_recharge;
        TextView tv_consume;
        TextView tv_remark;
    }
}
