package com.baima.massagemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

public class SelectStaffAdapter extends BaseAdapter {

    private Context context;
    private List<String> titleList;
    private boolean moreSelect;

    public SelectStaffAdapter(Context context, List<String> titleList) {
        this.context = context;
        this.titleList = titleList;
    }

    /**
     * 设置多选模式
     *
     * @param moreSelect
     */
    public void setMoreSelect(boolean moreSelect) {
        this.moreSelect = moreSelect;
        notifyDataSetChanged();
    }

    public boolean isMoreSelect() {
        return moreSelect;
    }

    @Override
    public int getCount() {
        return titleList.size();
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_select_staff, parent, false);
            holder = new ViewHolder();
            holder.tv = convertView.findViewById(R.id.tv);
            holder.cb = convertView.findViewById(R.id.cb);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String s = titleList.get(position);
        holder.tv.setText(s);
        if (moreSelect) {
            holder.cb.setVisibility(View.VISIBLE);
        } else {
            holder.cb.setVisibility(View.GONE);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView tv;
        CheckBox cb;
    }
}
