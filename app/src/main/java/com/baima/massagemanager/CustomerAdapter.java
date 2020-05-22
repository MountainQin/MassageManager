package com.baima.massagemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.baima.massagemanager.entity.Customer;

import java.util.List;

public class CustomerAdapter extends BaseAdapter {

    private Context context;
    private List<Customer> customerList;

    public CustomerAdapter(Context context, List<Customer> customerList) {
        this.context = context;
        this.customerList = customerList;
    }

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
        ViewHolder holder=null;
        if (convertView==null){
            holder=new ViewHolder();
            convertView=LayoutInflater.from(context)
                    .inflate(R.layout.item_customer, parent, false);
            holder.tv_number=convertView.findViewById(R.id.tv_number);
            holder.tv_name=convertView.findViewById(R.id.tv_name);
            holder.tv_remainder=convertView.findViewById(R.id.tv_remainder);
            holder.tv_recharge =convertView.findViewById(R.id.tv_recharge);
                    holder.tv_consume =convertView.findViewById(R.id.tv_consume);
                    holder.tv_remark=convertView.findViewById(R.id.tv_remark);
                    convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }

        Customer customer = customerList.get(position);
        holder.tv_number.setText(String .valueOf(customer.getNumber()));
        holder.tv_name.setText(customer.getName());
        //剩余次数的小数部分如果是0就不显示 小数部分
        double remainder = customer.getRemainder();
        int i=new Double(remainder).intValue();
        if (remainder==i) {
            holder.tv_remainder.setText("剩余：" +i +"小时");
        } else {
            holder.tv_remainder.setText("剩余：" +remainder +"小时");
        }
        holder.tv_remark.setText(customer.getRemark());

holder.tv_recharge.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Toast.makeText(context, "click recharge", Toast.LENGTH_SHORT).show();
    }
});

holder.tv_consume.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Toast.makeText(context, "click consume", Toast.LENGTH_SHORT).show();
    }
});
        return convertView;
    }

    static class ViewHolder{
TextView tv_number;
TextView tv_name;
TextView tv_remainder;
TextView tv_recharge;
TextView tv_consume;
TextView tv_remark;
    }
}
