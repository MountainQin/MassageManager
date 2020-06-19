package com.baima.massagemanager;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.Staff;
import com.baima.massagemanager.entity.WorkStaff;
import com.baima.massagemanager.util.ConsumeRecordUtil;
import com.baima.massagemanager.util.StringUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class StaffMessageActivity extends BaseActivity<Staff, ConsumeRecord> {

    private TextView tv_record;
    private TextView tv_current_month_time;

    @Override
    public void initViews() {
        super.initViews();
        tv_record = findViewById(R.id.tv_record);
        tv_current_month_time = findViewById(R.id.tv_current_month_time);

        tv_record.setVisibility(View.VISIBLE);
        tv_current_month_time.setVisibility(View.VISIBLE);
        tv_record.setOnClickListener(this);
        tv_current_month_time.setOnClickListener(this);
    }

    @Override
    public void refreshBaseMessage() {
        super.refreshBaseMessage();
        //设置员工ID
        ((StaffRecordAdapter) adapter).setStaffId(t.getId());
        double hoursOfCurrentMonth = t.getHoursOfCurrentMonth();
        tv_current_month_time.setText("本月：" + StringUtil.doubleTrans(hoursOfCurrentMonth) + "小时");
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        Intent intent = null;
        switch (v.getId()) {
            case R.id.tv_record:
                //打开记钟界面
                intent = new Intent(this, RecordActivity.class);
                intent.putExtra("staffId", t.getId());
                startActivityForResult(intent, RECORD);
                break;
            case R.id.tv_current_month_time:
                intent = new Intent(this, EditActivity.class);
                startActivityForResult(intent, ALTER_CURRENT_MONTH_TIME);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RECORD:
                    refreshBaseMessage();
                    refreshListData(startTimeInMillis, endTimeInMillis);
                    return;
                case ALTER_CURRENT_MONTH_TIME:
                    double currentMonthTime = Double.valueOf(data.getStringExtra("inputData"));
                    t.setHoursOfCurrentMonth(currentMonthTime);
                    if (currentMonthTime == 0) {
                        t.setToDefault("hoursOfCurrentMonth");
                    }
                    t.update(t.getId());
                    refreshBaseMessage();
                    MainActivity.staffFragment.refreshListData();
                    return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public List<Staff> getTList(long personId) {
        return LitePal.where("id=?", String.valueOf(personId)).find(Staff.class);
    }

    @Override
    public RecyclerView.Adapter getAdapter(List<ConsumeRecord> dataList) {
        return new StaffRecordAdapter(this, dataList);
    }

    @Override
    public void loadMoreLeast20() {
        List list = new ArrayList<>();
        calendar.setTimeInMillis(startTimeInMillis);
        while (list.size() < 20) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            if (calendar.get(Calendar.YEAR) < 2020) {
                break;
            }
            list.addAll(getConsumeRecordList(calendar.getTimeInMillis(), startTimeInMillis));
            startTimeInMillis = calendar.getTimeInMillis();
        }
        dataList.addAll(list);
        lRecyclerViewAdapter.notifyDataSetChanged();

    }

    @Override
    public void refreshListData(long startTimeInMillis, long endTimeInMillis) {
        dataList.clear();
        dataList.addAll(getConsumeRecordList(startTimeInMillis, endTimeInMillis));
        lRecyclerViewAdapter.notifyDataSetChanged();
    }

    //获取指定起始时间的消费记录
    private List<ConsumeRecord> getConsumeRecordList(long startTimeInMillis, long endTimeInMillis) {
        List<ConsumeRecord> consumeRecordList = new ArrayList<>();
        List<ConsumeRecord> list = LitePal.where("consumeTimestamp>=? and consumeTimestamp<?", String.valueOf(startTimeInMillis), String.valueOf(endTimeInMillis))
                .order("id desc").find(ConsumeRecord.class);
        //判断 每条记录，如果工作员工有这个员工就添加到集合
        for (int i = 0; i < list.size(); i++) {
            ConsumeRecord consumeRecord = list.get(i);
            List<WorkStaff> workStaffList = LitePal.where("consumeRecordId=? and staffId=?", String.valueOf(consumeRecord.getId()), String.valueOf(t.getId())).find(WorkStaff.class);
if (workStaffList.size()>0){
    consumeRecordList.add(consumeRecord);
}
        }
        return ConsumeRecordUtil.sortConsumeTimestampDesc(consumeRecordList);
    }

}
