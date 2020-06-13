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
import com.baima.massagemanager.util.ConsumeRecordUtil;
import com.baima.massagemanager.util.PersonUtil;
import com.baima.massagemanager.util.StringUtil;
import com.github.jdsjlzx.interfaces.OnItemLongClickListener;
import com.github.jdsjlzx.interfaces.OnLoadMoreListener;
import com.github.jdsjlzx.recyclerview.LRecyclerView;
import com.github.jdsjlzx.recyclerview.LRecyclerViewAdapter;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public abstract class BaseActivity<T extends Person, E> extends AppCompatActivity implements View.OnClickListener, OnItemLongClickListener, OnLoadMoreListener {

    public static final String EXTRA_PERSON_ID = "personId";
    private static final int RECORD = 3;
    private static final int ALTER_NUMBER = 5;
    private static final int ALTER_NAME = 6;
    private static final int ALTER_PHONE_NUMBER = 7;
    private static final int ALTER_CURRENT_MONTH_TIME = 8;
    private static final int ALTER_REMARK = 9;
    private static final int PICK_DATE = 10;

    private Staff staff;

    public List<ConsumeRecord> consumeRecordList = new ArrayList<>();

    private TextView tv_delete;
    private LRecyclerView lrv_staffer_record;
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
    public TextView tv_recharge;
    public TextView tv_consume;
    public TextView tv_remainder;
    public T t;
    public List<E> dataList=new ArrayList<>();

    public abstract List<T> getTList(long personId);
    public abstract RecyclerView.Adapter getAdapter(List<E> dataList);
    public abstract  void loadMoreLeast20();
    public abstract  void refreshListData(long startTimeInMillis, long endTimeInMillis) ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_base);

        initViews();
    }

    @Override
    public void onItemLongClick(View view, int position) {
        showDeleteRecordDialog(position);
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
                showDeleteStaffDialog();
                break;
            case R.id.tv_consume:
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

            //刷新 基本信息和列表
            refreshBaseMessage();
            refreshListData(startTimeInMillis, endTimeInMillis);

        }
    }


    public void initViews() {
        tv_delete = findViewById(R.id.tv_delete);
        tv_search = findViewById(R.id.tv_search);
        tv_recharge = findViewById(R.id.tv_recharge);
        tv_consume = findViewById(R.id.tv_consume);
        tv_record = findViewById(R.id.tv_record);

        tv_number = findViewById(R.id.tv_number);
        tv_name = findViewById(R.id.tv_name);
        tv_phone_number = findViewById(R.id.tv_phone_number);
        tv_call = findViewById(R.id.tv_call);
        tv_sms = findViewById(R.id.tv_sms);
        tv_remainder = findViewById(R.id.tv_remainder);
        tv_current_month_time = findViewById(R.id.tv_current_month_time);
        tv_remark = findViewById(R.id.tv_remark);
        tv_date = findViewById(R.id.tv_date);
        lrv_staffer_record = findViewById(R.id.lrv_customer_record);

        Intent intent = getIntent();
        long personId = intent.getLongExtra(EXTRA_PERSON_ID, 0);
        List<T> tList = getTList(personId);


        //如果 没有找到顾客员工，退出
        if (tList.size() == 0) {
            finish();
        }

         t = tList.get(0);
        refreshBaseMessage();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        lrv_staffer_record.setLayoutManager(linearLayoutManager);
        adapter=getAdapter(dataList);
        lRecyclerViewAdapter = new LRecyclerViewAdapter(adapter);
        lrv_staffer_record.setAdapter(lRecyclerViewAdapter);

        initData();
        loadMoreLeast20();
        refreshTvDate();

        tv_delete.setOnClickListener(this);
        tv_record.setOnClickListener(this);

        tv_number.setOnClickListener(this);
        tv_name.setOnClickListener(this);
        tv_phone_number.setOnClickListener(this);
        tv_current_month_time.setOnClickListener(this);
        tv_remark.setOnClickListener(this);
        tv_date.setOnClickListener(this);

        lrv_staffer_record.setOnLoadMoreListener(this);
        lRecyclerViewAdapter.setOnItemLongClickListener(this);
    }

    //    刷新 列表数据
    private void refreshListData() {
        consumeRecordList.clear();
        List<ConsumeRecord> list = LitePal.where("staffId=?", String.valueOf(staffId))
                .order("id desc").find(ConsumeRecord.class);
        consumeRecordList.addAll(ConsumeRecordUtil.sortConsumeTimestampDesc(list));
        adapter.notifyDataSetChanged();
    }

    private void showDeleteStaffDialog() {
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

    //删除记录对话框
    private void showDeleteRecordDialog(final int position) {
        new AlertDialog.Builder(BaseActivity.this)
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
                        //刷新 记录列表
                        MainActivity.recordFragment.removeConsumeRecord(consumeRecord);
                    }
                })
                .show();


    }

    //初始化数据
    private void initData() {
        calendar = Calendar.getInstance();
        //小时分钟秒毫秒清零
        CalendarUtil.setTimeTo0(calendar);
//           startTimeInMillis = calendar.getTimeInMillis();
//往后一天
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        startTimeInMillis = calendar.getTimeInMillis();
        endTimeInMillis = calendar.getTimeInMillis();
    }

    //往前一天加载，至少20
    private void loadMoreLeast201() {
        List<ConsumeRecord> list = new ArrayList<>();
        calendar.setTimeInMillis(startTimeInMillis);
        while (list.size() < 20) {
            calendar.add(Calendar.DAY_OF_MONTH, -1);
            if (calendar.get(Calendar.YEAR) < 2020) {
                break;
            }
            list.addAll(ConsumeRecordUtil.getConsumeRecordListPreviousDay(startTimeInMillis, staffId));
            startTimeInMillis = calendar.getTimeInMillis();
        }
        consumeRecordList.addAll(list);
        adapter.notifyDataSetChanged();
    }

    //刷新 日期标签
    private void refreshTvDate() {
        String s = new Date(startTimeInMillis).toLocaleString();
        String s1 = new Date(endTimeInMillis).toLocaleString();
        tv_date.setText(s + " - " + s1);
    }


    //根据指定起始时间戳刷新 列表数据
    private void refreshListData1(long startTimeInMillis, long endTimeInMillis) {
        consumeRecordList.clear();
        List<ConsumeRecord> list = LitePal.where("consumeTimestamp >=? and consumeTimestamp<? and staffId=?", String.valueOf(startTimeInMillis), String.valueOf(endTimeInMillis), String.valueOf(staffId))
                .order("id desc").find(ConsumeRecord.class);
        consumeRecordList.addAll(ConsumeRecordUtil.sortConsumeTimestampDesc(list));
        adapter.notifyDataSetChanged();
    }

    public void refreshBaseMessage() {
        tv_number.setText("编号：" + t.getNumber());
        tv_name.setText("姓名：" + t.getName());
        tv_phone_number.setText("手机号：" + t.getPhoneNumber());
        tv_remark.setText("备注：" + t.getRemark());
    }
}
