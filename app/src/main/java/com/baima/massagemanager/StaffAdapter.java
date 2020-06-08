package com.baima.massagemanager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baima.massagemanager.entity.Staff;
import com.baima.massagemanager.util.StringUtil;

import java.util.List;

public class StaffAdapter extends BaseAdapter {

    private Context context;
    private List<Staff> staffList;
    private OnItemListener onItemListener;
    public double hourPercentage;

    public StaffAdapter(Context context, List<Staff> staffList) {
        this.context = context;
        this.staffList = staffList;
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        hourPercentage = defaultSharedPreferences.getFloat("hourPercentage", 0);
    }

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    @Override
    public int getCount() {
        return staffList.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_staff, parent, false);
            holder = new ViewHolder();
            holder.tv_number = convertView.findViewById(R.id.tv_number);
            holder.tv_name = convertView.findViewById(R.id.tv_name);
            holder.tv_current_month_time = convertView.findViewById(R.id.tv_current_month_time);
            holder.tv_current_month_amount = convertView.findViewById(R.id.tv_current_month_amount);
            holder.tv_record = convertView.findViewById(R.id.tv_record);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final Staff staff = staffList.get(position);
        holder.tv_number.setText(staff.getNumber() + "号");
        holder.tv_name.setText(staff.getName());
        double currentMonthTime = staff.getHoursOfCurrentMonth();
        String currentMonthTimeStr = StringUtil.doubleTrans(staff.getHoursOfCurrentMonth());
        holder.tv_current_month_time.setText("本月：" + currentMonthTimeStr + "小时");
        String amountStr = StringUtil.doubleTrans(hourPercentage * currentMonthTime, true);
        holder.tv_current_month_amount.setText("合计：" + amountStr);
        holder.tv_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemListener.onRecordClick(staff.getId());
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView tv_number;
        TextView tv_name;
        TextView tv_current_month_time;
        TextView tv_current_month_amount;
        TextView tv_record;
    }

    /**
     * 项目的监听器
     */
    public interface OnItemListener {
        /**
         * 记钟的点击
         *
         * @param staffId
         */
        public abstract void onRecordClick(long staffId);
    }
}
