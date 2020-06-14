package com.baima.massagemanager;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baima.massagemanager.entity.ConsumeRecord;
import com.baima.massagemanager.entity.Person;
import com.baima.massagemanager.entity.Staff;
import com.baima.massagemanager.util.CalendarUtil;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class BaseActivity<T extends Person, E> extends AppCompatActivity implements View.OnClickListener, OnLoadMoreListener {

    public static final String EXTRA_PERSON_ID = "personId";

    public static final int RECHARGE = 1;
    public static final int CONSUME = 2;
    private static final int RECORD = 3;
    private static final int ALTER_NUMBER = 5;
    private static final int ALTER_NAME = 6;
    private static final int ALTER_PHONE_NUMBER = 7;
    private static final int ALTER_CURRENT_MONTH_TIME = 8;
    private static final int ALTER_REMARK = 9;
    private static final int PICK_DATE = 10;
    public static final int ALTER_REMAINDER=11;

    private Staff staff;

    public List<ConsumeRecord> consumeRecordList = new ArrayList<>();

    private TextView tv_delete;
    public LRecyclerView lrv_staffer_record;
    private TextView tv_search;
    private TextView tv_record;
    private TextView tv_number;
    private TextView tv_name;
    private TextView tv_phone_number;
    private TextView tv_call;
    private TextView tv_sms;
    private TextView tv_current_month_time;
    private TextView tv_remark;
    public RecyclerView.Adapter adapter;
    public long staffId;
    public LRecyclerViewAdapter lRecyclerViewAdapter;
    private TextView tv_date;
    public Calendar calendar;
    public long startTimeInMillis;
    public long endTimeInMillis;
    public T t;
    public List<E> dataList = new ArrayList<>();

    public abstract List<T> getTList(long personId);

    public abstract RecyclerView.Adapter getAdapter(List<E> dataList);

    public abstract void loadMoreLeast20();

    public abstract void refreshListData(long startTimeInMillis, long endTimeInMillis);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_base);

        initViews();
        initData();
    }

    @Override
    public void onLoadMore() {
        loadMoreLeast20();
        lrv_staffer_record.refreshComplete(0);

        LinearLayoutManager layoutManager = (LinearLayoutManager) lrv_staffer_record.getLayoutManager();
        if (layoutManager.findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
            Toast.makeText(this, "已经加载到2020年1月1日！", Toast.LENGTH_SHORT).show();
        }

        refreshTvDate();
    }

    @Override
    public void onClick(View v) {
        Intent editActivityIntent = null;

        switch (v.getId()) {
            case R.id.tv_delete:
                showDeletePersonDialog();
                break;
            case R.id.tv_record:
                //打开记钟界面
                Intent intent = new Intent(this, RecordActivity.class);
                intent.putExtra("staffId", staffId);
                startActivityForResult(intent, RECORD);
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
            case R.id.tv_date:
                intent = new Intent(this, PickDateActivity.class);
                intent.putExtra(PickDateActivity.START_TIME_IN_MILLIS, startTimeInMillis);
                intent.putExtra(PickDateActivity.END_TIME_IN_MILLIS, endTimeInMillis);
                startActivityForResult(intent, PICK_DATE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK, getIntent());

//            refreshListData();在后面
            //刷新 基本信息
            String inputData = data.getStringExtra("inputData");
            switch (requestCode) {
                case PICK_DATE:
                    startTimeInMillis = data.getLongExtra(PickDateActivity.START_TIME_IN_MILLIS, startTimeInMillis);
                    endTimeInMillis = data.getLongExtra(PickDateActivity.END_TIME_IN_MILLIS, endTimeInMillis);
                    refreshTvDate();
                    break;

                case ALTER_NUMBER:
                    int number = Integer.valueOf(inputData);
                    if (number == 0) {
                        startActivityForResult(data, ALTER_NUMBER);
                        return;
                    }
                    t.setNumber(number);
                    t.update(t.getId());
                    refreshBaseMessage();
                    return;
                case ALTER_NAME:
                    t.setName(inputData);
                    t.update(t.getId());
                    refreshBaseMessage();
                    return;
                case ALTER_PHONE_NUMBER:
                    t.setPhoneNumber(inputData);
                    t.update(t.getId());
                    refreshBaseMessage();
                    return;
                case ALTER_REMARK:
                    t.setRemark(inputData);
                    t.update(t.getId());
                    refreshBaseMessage();
                    return;
            }

            //刷新 基本信息和列表
            refreshBaseMessage();
            refreshListData(startTimeInMillis, endTimeInMillis);

        }
    }


    public void initViews() {
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
        tv_date = findViewById(R.id.tv_date);
        lrv_staffer_record = findViewById(R.id.lrv_customer_record);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lrv_staffer_record.setLayoutManager(linearLayoutManager);
        adapter = getAdapter(dataList);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        lrv_staffer_record.setAdapter(lRecyclerViewAdapter);

        tv_delete.setOnClickListener(this);
        tv_record.setOnClickListener(this);

        tv_number.setOnClickListener(this);
        tv_name.setOnClickListener(this);
        tv_phone_number.setOnClickListener(this);
        tv_current_month_time.setOnClickListener(this);
        tv_remark.setOnClickListener(this);
        tv_date.setOnClickListener(this);

        lrv_staffer_record.setOnLoadMoreListener(this);
    }

    private void initData() {
        Intent intent = getIntent();
        long personId = intent.getLongExtra(EXTRA_PERSON_ID, 0);
        List<T> tList = getTList(personId);


        //如果 没有找到顾客员工，退出
        if (tList.size() == 0) {
            finish();
        }

        t = tList.get(0);
        refreshBaseMessage();

        initDate();
        loadMoreLeast20();
        refreshTvDate();
    }

    //删除顾客 或者员工的对话框
    private void showDeletePersonDialog() {
        String numberName = t.getNumber() + "号" + t.getName();
        String msg = "你确定删除 " + numberName + " 吗？";
        new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage(msg)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        t.delete();
                        setResult(RESULT_OK);
                        finish();
                    }
                })
                .show();

    }

    //初始化时间
    private void initDate() {
        calendar = Calendar.getInstance();
        //小时分钟秒毫秒清零
        CalendarUtil.setTimeTo0(calendar);
//           startTimeInMillis = calendar.getTimeInMillis();
//往后一天
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        startTimeInMillis = calendar.getTimeInMillis();
        endTimeInMillis = calendar.getTimeInMillis();
    }

    //刷新 日期标签
    private void refreshTvDate() {
        String s = new Date(startTimeInMillis).toLocaleString();
        String s1 = new Date(endTimeInMillis).toLocaleString();
        tv_date.setText(s + " - " + s1);
    }

    //刷新基本信息
    public void refreshBaseMessage() {
        t = getTList(t.getId()).get(0);
        tv_number.setText("编号：" + t.getNumber());
        tv_name.setText("姓名：" + t.getName());
        tv_phone_number.setText("手机号：" + t.getPhoneNumber());
        tv_remark.setText("备注：" + t.getRemark());
    }
}
