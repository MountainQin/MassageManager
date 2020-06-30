package com.baima.massagemanager;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    private static final String TAG = "baima";
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
    public void loadMoreLeast50() {
        lrv_staffer_record.setLoadMoreEnabled(false);
//showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                List list = new ArrayList<>();
                calendar.setTimeInMillis(startTimeInMillis);
                while (list.size() < 50) {
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    if (calendar.get(Calendar.YEAR) < 2020) {
                        showToast("已经加载到2020年1月1日");
                        break;
                    }
                    list.addAll(getConsumeRecordList(calendar.getTimeInMillis(), startTimeInMillis));
                    startTimeInMillis = calendar.getTimeInMillis();
                }
                dataList.addAll(list);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //显示 记录数量
                        tv_lrv.setText("记录列表 " + dataList.size());
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        refreshTvDate();
                        closeProgressDialog();
                        lrv_staffer_record.refreshComplete(0);
                        lrv_staffer_record.setLoadMoreEnabled(true);
                    }
                });
            }
        }).start();

    }

    @Override
    public void refreshListData(final long startTimeInMillis, final long endTimeInMillis) {
        dataList.clear();
        showProgressDialog();
        new Thread(new Runnable() {
            @Override
            public void run() {
                dataList.addAll(getConsumeRecordList(startTimeInMillis, endTimeInMillis));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        lRecyclerViewAdapter.notifyDataSetChanged();
                        lrv_staffer_record.scrollToPosition(0);
                        refreshTvDate();
//显示 记录数量
                        tv_lrv.setText("记录列表 " + dataList.size());
                        closeProgressDialog();
                    }
                });
            }
        }).start();
    }

    //获取指定起始时间的消费记录
    private List<ConsumeRecord> getConsumeRecordList(long startTimeInMillis, long endTimeInMillis) {
        List<ConsumeRecord> consumeRecordList = new ArrayList<>();
        long staffId = t.getId();
        List<WorkStaff> all = LitePal.findAll(WorkStaff.class);
        for (WorkStaff workStaff : all) {
            Log.i(TAG, "getConsumeRecordList: "+workStaff.toString());
            Log.i(TAG, "getConsumeRecordList: "+workStaff.getConsumeTimestamp());
        }
        List<WorkStaff> list = LitePal.where("staffId=? and consumeTimestamp>=? and consumeTimestamp<?", String.valueOf(staffId), String.valueOf(startTimeInMillis), String.valueOf(endTimeInMillis))
                .order("id desc").find(WorkStaff.class);
        Log.i(TAG, "getConsumeRecordList: work staffs size "+list.size());
        //根据工作员工记录里的消费记录ID查找 消费记录
        for (WorkStaff workStaff : list) {
            ConsumeRecord consumeRecord = LitePal.find(ConsumeRecord.class, workStaff.getConsumeRecordId());
            if (consumeRecord != null) {
                consumeRecordList.add(consumeRecord);
            }
        }
        return ConsumeRecordUtil.sortConsumeTimestampDesc(consumeRecordList);
    }

    private List<ConsumeRecord> getConsumeRecordList1(long startTimeInMillis, long endTimeInMillis) {
        List<ConsumeRecord> consumeRecordList = new ArrayList<>();
        long staffId = t.getId();
        List<WorkStaff> list = LitePal.where("staffId=? and consumeTimestamp>=? and consumeTimestamp<?", String.valueOf(staffId), String.valueOf(startTimeInMillis), String.valueOf(endTimeInMillis))
                .order("id desc").find(WorkStaff.class);
        //根据工作员工记录里的消费记录ID查找 消费记录
        long[] ids = new long[list.size()];
        for (int i = 0; i < list.size(); i++) {
            ids[i] = list.get(i).getConsumeRecordId();
        }
        consumeRecordList = LitePal.findAll(ConsumeRecord.class, ids);
        return ConsumeRecordUtil.sortConsumeTimestampDesc(consumeRecordList);
    }

}
