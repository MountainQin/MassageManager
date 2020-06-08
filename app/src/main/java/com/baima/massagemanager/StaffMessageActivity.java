package com.baima.massagemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.Staff;
import com.baima.massagemanager.util.PersonUtil;
import com.baima.massagemanager.util.StringUtil;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class StaffMessageActivity extends AppCompatActivity implements View.OnClickListener, StaffRecordAdapter.OnItemDeleteListener {

    private static final int ALTER_NUMBER = 5;
    private static final int ALTER_NAME = 6;
    private static final int ALTER_PHONE_NUMBER = 7;
    private static final int ALTER_CURRENT_MONTH_TIME = 8;
    private static final int ALTER_REMARK = 9;

    private List<ConsumeRecord> consumeRecordList = new ArrayList<>();
    private TextView tv_delete;
    private RecyclerView rv_staffer_record;
    private TextView tv_search;
    private TextView tv_record;
    private TextView tv_number;
    private TextView tv_name;
    private TextView tv_phone_number;
    private TextView tv_call;
    private TextView tv_sms;
    private TextView tv_current_month_time;
    private TextView tv_remark;
    private StaffRecordAdapter adapter;
    private long staffId;
    private Staff staff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("员工信息");
        setContentView(R.layout.activity_staff_message);

        initViews();
    }

    @Override
    public void onClick(View v) {
        Intent editActivityIntent = null;

        switch (v.getId()) {
            case R.id.tv_delete:
                showDeleteStaffDialog();
                break;

            case R.id.tv_number:
                editActivityIntent = new Intent(this, EditActivity.class);
                editActivityIntent.putExtra("inputType", InputType.TYPE_CLASS_NUMBER);
                startActivityForResult(editActivityIntent, ALTER_NUMBER);
                break;
            case R.id.tv_name:
                //修改姓名
                editActivityIntent = new Intent(this, EditActivity.class);
                editActivityIntent.putExtra("inputType", InputType.TYPE_CLASS_TEXT);
                startActivityForResult(editActivityIntent, ALTER_NAME);
                break;
            case R.id.tv_phone_number:
                editActivityIntent = new Intent(this, EditActivity.class);
                editActivityIntent.putExtra("inputType", InputType.TYPE_CLASS_PHONE);
                startActivityForResult(editActivityIntent, ALTER_PHONE_NUMBER);
                break;
            case R.id.tv_current_month_time:
                editActivityIntent = new Intent(this, EditActivity.class);
                startActivityForResult(editActivityIntent, ALTER_CURRENT_MONTH_TIME);
                break;
            case R.id.tv_remark:
                editActivityIntent = new Intent(this, EditActivity.class);
                editActivityIntent.putExtra("inputType", InputType.TYPE_CLASS_TEXT);
                startActivityForResult(editActivityIntent, ALTER_REMARK);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            ;
            String inputData = data.getStringExtra("inputData");
            switch (requestCode) {
                case ALTER_NUMBER:
                    int number = Integer.valueOf(inputData);
                    if (number == 0) {
                        startActivityForResult(data, ALTER_NUMBER);
                        return;
                    }
                    staff.setNumber(number);
                    staff.update(staff.getId());
                    refreshBaseMessage();
                    return;
                case ALTER_NAME:
                    staff.setName(inputData);
                    staff.update(staff.getId());
                    refreshBaseMessage();
                    return;
                case ALTER_PHONE_NUMBER:
                    staff.setPhoneNumber(inputData);
                    staff.update(staff.getId());
                    refreshBaseMessage();
                    return;
                case ALTER_CURRENT_MONTH_TIME:
                    double currentMonthTime = Double.valueOf(inputData);
                    staff.setHoursOfCurrentMonth(currentMonthTime);
                    if (currentMonthTime == 0) {
                        staff.setToDefault("hoursOfCurrentMonth");
                    }
                    staff.update(staff.getId());
                    refreshBaseMessage();
                    return;
                case ALTER_REMARK:
                    staff.setRemark(inputData);
                    staff.update(staff.getId());
                    refreshBaseMessage();
                    return;

            }
        }
    }

    @Override
    public void onItemLongClick(int position) {
        showDeleteRecordDialog(position);
    }

    private void initViews() {
        tv_delete = findViewById(R.id.tv_delete);
        tv_search = findViewById(R.id.tv_search);
        tv_record = findViewById(R.id.tv_record);

        tv_number = findViewById(R.id.tv_number);
        tv_name = findViewById(R.id.tv_name);
        tv_phone_number = findViewById(R.id.tv_phone_number);
        tv_call = findViewById(R.id.tv_call);
        tv_sms = findViewById(R.id.tv_sms);
        tv_current_month_time = findViewById(R.id.tv_current_month_time);
        tv_remark = findViewById(R.id.tv_remark);
        rv_staffer_record = findViewById(R.id.rv_staffer_record);

        Intent intent = getIntent();
        staffId = intent.getLongExtra("staffId", 0);
        refreshBaseMessage();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rv_staffer_record.setLayoutManager(linearLayoutManager);
        adapter = new StaffRecordAdapter(this, consumeRecordList);
        rv_staffer_record.setAdapter(adapter);
        refreshListData();
        adapter.setOnItemDeleteListener(this);
        tv_delete.setOnClickListener(this);

        tv_number.setOnClickListener(this);
        tv_name.setOnClickListener(this);
        tv_phone_number.setOnClickListener(this);
        tv_current_month_time.setOnClickListener(this);
        tv_remark.setOnClickListener(this);
    }

    //刷新 基本信息
    private void refreshBaseMessage() {
        List<Staff> staffList = LitePal.where("id=?", String.valueOf(staffId)).find(Staff.class);
        if (staffList.size() > 0) {
            staff = staffList.get(0);
            tv_number.setText("编号：" + staff.getNumber());
            tv_name.setText("姓名：" + staff.getName());
            tv_phone_number.setText("手机号：" + staff.getPhoneNumber());
            double hoursOfCurrentMonth = staff.getHoursOfCurrentMonth();
            tv_current_month_time.setText("本月：" + StringUtil.doubleTrans(hoursOfCurrentMonth) + "小时");
            tv_remark.setText("备注：" + staff.getRemark());
        }
    }

    //刷新 列表数据
    private void refreshListData() {
        consumeRecordList.clear();
        consumeRecordList.addAll(
                LitePal.where("staffId=?", String.valueOf(staffId))
                        .order("consumeTimestamp desc").find(ConsumeRecord.class)
        );
        adapter.notifyDataSetChanged();
    }

    private void showDeleteStaffDialog() {
        String numberName = staff.getNumber() + "号" + staff.getName();
        String msg = "你确定删除 " + numberName + " 吗？";
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(msg)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        staff.delete();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .show();

    }

    //删除记录对话框
    private void showDeleteRecordDialog(final int position) {
        new AlertDialog.Builder(StaffMessageActivity.this)
                .setTitle("提示")
                .setMessage("你确定删除这条记录吗？")
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //从消费记录数据 表删除
                        ConsumeRecord consumeRecord = consumeRecordList.get(position);
                        consumeRecord.delete();
                        //修改员工时间，减少
                        double workTime = consumeRecord.getWorkTime();
                        PersonUtil.updateStaffTime(staffId, -workTime);

                        refreshBaseMessage();
                        consumeRecordList.remove(position);
                        adapter.notifyDataSetChanged();
                        setResult(RESULT_OK);

                        //修改顾客 时间
                        long customerId = consumeRecord.getCustomerId();
                        PersonUtil.updateCustomerTime(customerId, workTime);
                        MainActivity.customerFragment.refreshCustomerList();
                    }
                })
                .show();


    }
}
